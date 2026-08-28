package com.abaxial.portal.events.service;

import com.abaxial.portal.audit.service.AuditoriaService;
import com.abaxial.portal.clients.entity.Cliente;
import com.abaxial.portal.clients.repository.ClienteRepository;
import com.abaxial.portal.common.exception.ResourceNotFoundException;
import com.abaxial.portal.events.dto.EventoDTO;
import com.abaxial.portal.events.dto.EventoRequestDTO;
import com.abaxial.portal.events.entity.Evento;
import com.abaxial.portal.events.repository.EventoRepository;
import com.abaxial.portal.users.entity.Usuario;
import com.abaxial.portal.users.repository.UsuarioRepository;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventoService {

    private final EventoRepository eventoRepository;
    private final ClienteRepository clienteRepository;
    private final UsuarioRepository usuarioRepository;
    private final AuditoriaService auditoriaService;
    private final RecurrenciaEngine recurrenciaEngine;

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ISO_LOCAL_DATE;

    @Transactional(readOnly = true)
    public List<EventoDTO> obtenerEventosCalendario(
            Long usuarioId, Long clienteId, String estado, String tipo,
            LocalDateTime desde, LocalDateTime hasta, boolean includeInactive,
            String currentUsername
    ) {
        Usuario currentUser = usuarioRepository.findByUsername(currentUsername).orElse(null);
        Long currentUserId = currentUser != null ? currentUser.getId() : -1L;

        Specification<Evento> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            // REGLA CRÍTICA DE PRIVACIDAD:
            Predicate isShared = cb.equal(root.get("visibilidad"), "COMPARTIDO");
            Predicate isOwner = cb.and(
                    cb.isNotNull(root.get("usuario")),
                    cb.equal(root.get("usuario").get("id"), currentUserId)
            );
            predicates.add(cb.or(isShared, isOwner));

            if (usuarioId != null) {
                predicates.add(cb.equal(root.get("usuario").get("id"), usuarioId));
            }

            if (clienteId != null) {
                predicates.add(cb.equal(root.get("cliente").get("id"), clienteId));
            }

            if (!includeInactive) {
                predicates.add(cb.isTrue(root.get("activo")));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        List<Evento> allEvents = eventoRepository.findAll(spec, Sort.by(Sort.Direction.ASC, "fechaInicio"));

        List<EventoDTO> resultado = new ArrayList<>();

        // Separar eventos puntuales vs recurrentes
        for (Evento ev : allEvents) {
            if ("RECURRENTE".equalsIgnoreCase(ev.getTipo()) && ev.getEventoPadreId() == null) {
                // Evento recurrente padre: buscar sus excepciones
                List<Evento> excepciones = eventoRepository.findByEventoPadreIdAndActivoTrue(ev.getId());
                Set<String> fechasSobrescritas = new HashSet<>();
                for (Evento exc : excepciones) {
                    if (exc.getFechaOriginalOcurrencia() != null) {
                        fechasSobrescritas.add(exc.getFechaOriginalOcurrencia().toLocalDate().format(DATE_FMT));
                    }
                }

                // Generar ocurrencias reales
                List<EventoDTO> ocurrencias = recurrenciaEngine.expandirOcurrencias(ev, desde, hasta, fechasSobrescritas);
                resultado.addAll(ocurrencias);
            } else if (ev.getEventoPadreId() != null && Boolean.TRUE.equals(ev.getEsExcepcion())) {
                // Es una excepción puntual a una recurrencia
                if (estaEnRango(ev.getFechaInicio(), desde, hasta)) {
                    resultado.add(EventoDTO.fromEntity(ev));
                }
            } else {
                // Es un evento puntual estándar
                if (estaEnRango(ev.getFechaInicio(), desde, hasta)) {
                    resultado.add(EventoDTO.fromEntity(ev));
                }
            }
        }

        // Aplicar filtros de tipo y estado si se especificaron
        return resultado.stream()
                .filter(e -> {
                    if (tipo != null && !tipo.isBlank()) {
                        if (!tipo.equalsIgnoreCase(e.getTipo())) return false;
                    }
                    if (estado != null && !estado.isBlank()) {
                        if (!estado.equalsIgnoreCase(e.getEstado())) return false;
                    }
                    return true;
                })
                .sorted(Comparator.comparing(EventoDTO::getFechaInicio))
                .collect(Collectors.toList());
    }

    private boolean estaEnRango(LocalDateTime fecha, LocalDateTime desde, LocalDateTime hasta) {
        if (fecha == null) return false;
        if (desde != null && fecha.isBefore(desde)) return false;
        if (hasta != null && fecha.isAfter(hasta)) return false;
        return true;
    }

    @Transactional(readOnly = true)
    public List<EventoDTO> listarPorCliente(Long clienteId, String currentUsername) {
        return obtenerEventosCalendario(null, clienteId, null, null, null, null, false, currentUsername);
    }

    @Transactional(readOnly = true)
    public EventoDTO obtenerPorId(Long id, String currentUsername) {
        Evento e = eventoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Evento no encontrado con ID: " + id));

        validatePrivacyAccess(e, currentUsername);
        return EventoDTO.fromEntity(e);
    }

    @Transactional
    public EventoDTO crearEvento(EventoRequestDTO req, String currentUsername) {
        Usuario usuario = usuarioRepository.findByUsername(currentUsername).orElse(null);
        Cliente cliente = null;
        if (req.getClienteId() != null) {
            cliente = clienteRepository.findById(req.getClienteId()).orElse(null);
        }

        String visibilidad = "COMPARTIDO";
        if (req.getVisibilidad() != null && !req.getVisibilidad().isBlank()) {
            String v = req.getVisibilidad().trim().toUpperCase();
            if ("PRIVADO".equals(v) || "PRIVATE".equals(v) || "SOLO_PARA_MI".equals(v)) {
                visibilidad = "PRIVADO";
            }
        }

        String tipo = req.getTipo() != null && !req.getTipo().isBlank()
                ? req.getTipo().trim().toUpperCase()
                : "PUNTUAL";

        Evento evento = Evento.builder()
                .usuario(usuario)
                .cliente(cliente)
                .titulo(req.getTitulo().trim())
                .descripcion(req.getDescripcion())
                .fechaInicio(req.getFechaInicio())
                .fechaFin(req.getFechaFin())
                .tipo(tipo)
                .recurrencia(req.getRecurrencia())
                .fechaFinRecurrencia(req.getFechaFinRecurrencia())
                .diasSemana(req.getDiasSemana())
                .diaMes(req.getDiaMes())
                .eventoPadreId(req.getEventoPadreId())
                .fechaOriginalOcurrencia(req.getFechaOriginalOcurrencia())
                .esExcepcion(Boolean.TRUE.equals(req.getEsExcepcion()))
                .fechasExcluidas(req.getFechasExcluidas())
                .prioridad(req.getPrioridad() != null && !req.getPrioridad().isBlank() ? req.getPrioridad().toUpperCase() : "MEDIA")
                .estado(req.getEstado() != null && !req.getEstado().isBlank() ? req.getEstado().toUpperCase() : "PENDIENTE")
                .visibilidad(visibilidad)
                .activo(req.getActivo() != null ? req.getActivo() : true)
                .fechaCreacion(LocalDateTime.now())
                .fechaModificacion(LocalDateTime.now())
                .build();

        Evento guardado = eventoRepository.save(evento);

        String detalleAuditoria = "PRIVADO".equals(visibilidad)
                ? "Evento privado agendado por el usuario"
                : "Evento " + tipo.toLowerCase() + " agendado: " + guardado.getTitulo() + (cliente != null ? " para " + cliente.getNombre() : "");

        auditoriaService.registrarAsync(
                currentUsername,
                "EVENTO_CREADO",
                "Evento",
                guardado.getId(),
                detalleAuditoria
        );

        return EventoDTO.fromEntity(guardado);
    }

    @Transactional
    public EventoDTO actualizarEvento(Long id, EventoRequestDTO req, String currentUsername) {
        Evento evento = eventoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Evento no encontrado con ID: " + id));

        validatePrivacyAccess(evento, currentUsername);

        if (req.getClienteId() != null) {
            Cliente cliente = clienteRepository.findById(req.getClienteId()).orElse(null);
            evento.setCliente(cliente);
        }

        if (req.getTitulo() != null && !req.getTitulo().isBlank()) {
            evento.setTitulo(req.getTitulo().trim());
        }
        if (req.getDescripcion() != null) {
            evento.setDescripcion(req.getDescripcion());
        }
        if (req.getFechaInicio() != null) {
            evento.setFechaInicio(req.getFechaInicio());
        }
        if (req.getFechaFin() != null) {
            evento.setFechaFin(req.getFechaFin());
        }
        if (req.getTipo() != null && !req.getTipo().isBlank()) {
            evento.setTipo(req.getTipo().trim().toUpperCase());
        }
        if (req.getRecurrencia() != null) {
            evento.setRecurrencia(req.getRecurrencia());
        }
        if (req.getFechaFinRecurrencia() != null) {
            evento.setFechaFinRecurrencia(req.getFechaFinRecurrencia());
        }
        if (req.getDiasSemana() != null) {
            evento.setDiasSemana(req.getDiasSemana());
        }
        if (req.getDiaMes() != null) {
            evento.setDiaMes(req.getDiaMes());
        }
        if (req.getPrioridad() != null && !req.getPrioridad().isBlank()) {
            evento.setPrioridad(req.getPrioridad().toUpperCase());
        }
        if (req.getEstado() != null && !req.getEstado().isBlank()) {
            evento.setEstado(req.getEstado().toUpperCase());
        }
        if (req.getVisibilidad() != null && !req.getVisibilidad().isBlank()) {
            String v = req.getVisibilidad().trim().toUpperCase();
            evento.setVisibilidad("PRIVADO".equals(v) || "PRIVATE".equals(v) || "SOLO_PARA_MI".equals(v) ? "PRIVADO" : "COMPARTIDO");
        }
        if (req.getActivo() != null) {
            evento.setActivo(req.getActivo());
        }

        evento.setFechaModificacion(LocalDateTime.now());
        Evento actualizado = eventoRepository.save(evento);

        String detalleAuditoria = "PRIVADO".equals(actualizado.getVisibilidad())
                ? "Evento privado modificado por su creador"
                : "Evento " + actualizado.getTipo().toLowerCase() + " modificado: " + actualizado.getTitulo();

        auditoriaService.registrarAsync(
                currentUsername,
                "EVENTO_MODIFICADO",
                "Evento",
                actualizado.getId(),
                detalleAuditoria
        );

        return EventoDTO.fromEntity(actualizado);
    }

    /**
     * Guarda o actualiza una excepción para una única ocurrencia de una serie recurrente.
     */
    @Transactional
    public EventoDTO crearOActualizarExcepcion(Long eventoPadreId, EventoRequestDTO req, String currentUsername) {
        Evento padre = eventoRepository.findById(eventoPadreId)
                .orElseThrow(() -> new ResourceNotFoundException("Evento recurrente padre no encontrado con ID: " + eventoPadreId));

        validatePrivacyAccess(padre, currentUsername);

        LocalDateTime fechaOriginal = req.getFechaOriginalOcurrencia() != null
                ? req.getFechaOriginalOcurrencia()
                : req.getFechaInicio();

        // Buscar si ya existe una excepción para esta fecha original
        List<Evento> excepciones = eventoRepository.findByEventoPadreId(eventoPadreId);
        Evento excepcionExistente = null;
        for (Evento exc : excepciones) {
            if (exc.getFechaOriginalOcurrencia() != null &&
                    exc.getFechaOriginalOcurrencia().toLocalDate().isEqual(fechaOriginal.toLocalDate())) {
                excepcionExistente = exc;
                break;
            }
        }

        Usuario usuario = usuarioRepository.findByUsername(currentUsername).orElse(null);
        Cliente cliente = padre.getCliente();
        if (req.getClienteId() != null) {
            cliente = clienteRepository.findById(req.getClienteId()).orElse(cliente);
        }

        Evento excepcion;
        if (excepcionExistente != null) {
            excepcion = excepcionExistente;
            excepcion.setTitulo(req.getTitulo() != null ? req.getTitulo().trim() : padre.getTitulo());
            excepcion.setDescripcion(req.getDescripcion() != null ? req.getDescripcion() : padre.getDescripcion());
            excepcion.setFechaInicio(req.getFechaInicio());
            excepcion.setFechaFin(req.getFechaFin());
            excepcion.setPrioridad(req.getPrioridad() != null ? req.getPrioridad().toUpperCase() : padre.getPrioridad());
            excepcion.setEstado(req.getEstado() != null ? req.getEstado().toUpperCase() : padre.getEstado());
            excepcion.setVisibilidad(padre.getVisibilidad());
            excepcion.setActivo(true);
            excepcion.setFechaModificacion(LocalDateTime.now());
        } else {
            excepcion = Evento.builder()
                    .usuario(usuario)
                    .cliente(cliente)
                    .titulo(req.getTitulo() != null ? req.getTitulo().trim() : padre.getTitulo())
                    .descripcion(req.getDescripcion() != null ? req.getDescripcion() : padre.getDescripcion())
                    .fechaInicio(req.getFechaInicio())
                    .fechaFin(req.getFechaFin())
                    .tipo("PUNTUAL")
                    .eventoPadreId(padre.getId())
                    .fechaOriginalOcurrencia(fechaOriginal)
                    .esExcepcion(true)
                    .prioridad(req.getPrioridad() != null ? req.getPrioridad().toUpperCase() : padre.getPrioridad())
                    .estado(req.getEstado() != null ? req.getEstado().toUpperCase() : padre.getEstado())
                    .visibilidad(padre.getVisibilidad())
                    .activo(true)
                    .fechaCreacion(LocalDateTime.now())
                    .fechaModificacion(LocalDateTime.now())
                    .build();
        }

        Evento guardado = eventoRepository.save(excepcion);

        auditoriaService.registrarAsync(
                currentUsername,
                "EVENTO_EXCEPCION_MODIFICADA",
                "Evento",
                guardado.getId(),
                "Modificó la ocurrencia del " + fechaOriginal.toLocalDate() + " de la serie '" + padre.getTitulo() + "'"
        );

        return EventoDTO.fromEntity(guardado);
    }

    /**
     * Excluye una única ocurrencia de una serie recurrente.
     */
    @Transactional
    public void excluirOcurrencia(Long eventoPadreId, String fechaOriginalIso, String currentUsername) {
        Evento padre = eventoRepository.findById(eventoPadreId)
                .orElseThrow(() -> new ResourceNotFoundException("Evento recurrente padre no encontrado con ID: " + eventoPadreId));

        validatePrivacyAccess(padre, currentUsername);

        String fechaKey = fechaOriginalIso.contains("T") ? fechaOriginalIso.split("T")[0] : fechaOriginalIso.trim();

        // Agregar a fechas excluidas
        String actual = padre.getFechasExcluidas();
        if (actual == null || actual.isBlank()) {
            padre.setFechasExcluidas(fechaKey);
        } else {
            Set<String> set = new HashSet<>(Arrays.asList(actual.split(",")));
            set.add(fechaKey);
            padre.setFechasExcluidas(String.join(",", set));
        }

        // Si existía un registro de excepción para esa fecha, desactivarlo también
        List<Evento> excepciones = eventoRepository.findByEventoPadreIdAndActivoTrue(eventoPadreId);
        for (Evento exc : excepciones) {
            if (exc.getFechaOriginalOcurrencia() != null &&
                    exc.getFechaOriginalOcurrencia().toLocalDate().format(DATE_FMT).equals(fechaKey)) {
                exc.setActivo(false);
                eventoRepository.save(exc);
            }
        }

        padre.setFechaModificacion(LocalDateTime.now());
        eventoRepository.save(padre);

        auditoriaService.registrarAsync(
                currentUsername,
                "EVENTO_OCURRENCIA_ELIMINADA",
                "Evento",
                padre.getId(),
                "Eliminó la ocurrencia del " + fechaKey + " de la serie '" + padre.getTitulo() + "'"
        );
    }

    @Transactional
    public void desactivarEvento(Long id, String currentUsername) {
        Evento evento = eventoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Evento no encontrado con ID: " + id));

        validatePrivacyAccess(evento, currentUsername);

        evento.setActivo(false);
        evento.setFechaModificacion(LocalDateTime.now());
        eventoRepository.save(evento);

        // Si es una serie padre, desactivar todas sus excepciones hijas
        if (evento.getEventoPadreId() == null && "RECURRENTE".equalsIgnoreCase(evento.getTipo())) {
            List<Evento> hijas = eventoRepository.findByEventoPadreIdAndActivoTrue(evento.getId());
            for (Evento h : hijas) {
                h.setActivo(false);
                h.setFechaModificacion(LocalDateTime.now());
                eventoRepository.save(h);
            }
        }

        String detalleAuditoria = "PRIVADO".equals(evento.getVisibilidad())
                ? "Evento privado eliminado por su creador"
                : "Evento cancelado/desactivado: " + evento.getTitulo();

        auditoriaService.registrarAsync(
                currentUsername,
                "EVENTO_DESACTIVADO",
                "Evento",
                evento.getId(),
                detalleAuditoria
        );
    }

    private void validatePrivacyAccess(Evento evento, String currentUsername) {
        if ("PRIVADO".equalsIgnoreCase(evento.getVisibilidad())) {
            boolean isOwner = evento.getUsuario() != null &&
                    evento.getUsuario().getUsername() != null &&
                    evento.getUsuario().getUsername().equalsIgnoreCase(currentUsername);

            if (!isOwner) {
                throw new ResourceNotFoundException("Evento no encontrado con ID: " + evento.getId());
            }
        }
    }
}

