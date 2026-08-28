package com.abaxial.portal.clients.service;

import com.abaxial.portal.audit.service.AuditoriaService;
import com.abaxial.portal.clients.dto.ClienteDetailDTO;
import com.abaxial.portal.clients.dto.ClienteListDTO;
import com.abaxial.portal.clients.dto.ClienteRequestDTO;
import com.abaxial.portal.clients.dto.DashboardStatsDTO;
import com.abaxial.portal.clients.entity.Cliente;
import com.abaxial.portal.clients.repository.ClienteRepository;
import com.abaxial.portal.common.dto.PaginatedResponse;
import com.abaxial.portal.common.exception.ConflictException;
import com.abaxial.portal.common.exception.ResourceNotFoundException;
import com.abaxial.portal.documents.repository.DocumentoRepository;
import com.abaxial.portal.equipment.repository.EquipoRepository;
import com.abaxial.portal.events.dto.EventoDTO;
import com.abaxial.portal.events.repository.EventoRepository;
import com.abaxial.portal.services.repository.ServicioRepository;
import com.abaxial.portal.users.entity.Usuario;
import com.abaxial.portal.users.repository.UsuarioRepository;
import com.abaxial.portal.websites.repository.WebRepository;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ClienteService {

    private final ClienteRepository clienteRepository;
    private final EquipoRepository equipoRepository;
    private final ServicioRepository servicioRepository;
    private final WebRepository webRepository;
    private final DocumentoRepository documentoRepository;
    private final UsuarioRepository usuarioRepository;
    private final EventoRepository eventoRepository;
    private final AuditoriaService auditoriaService;

    @Transactional(readOnly = true)
    public PaginatedResponse<ClienteListDTO> listarClientes(
            String search, String estado, String mantenimiento, String provincia,
            boolean includeInactive, Pageable pageable
    ) {
        Page<Cliente> page = clienteRepository.findAllFiltered(search, estado, mantenimiento, provincia, includeInactive, pageable);
        return PaginatedResponse.from(page.map(ClienteListDTO::fromEntity));
    }

    @Transactional(readOnly = true)
    public ClienteDetailDTO obtenerDetalleCliente(Long id) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado con ID: " + id));
        return ClienteDetailDTO.fromEntity(cliente);
    }

    @Transactional(readOnly = true)
    public Cliente obtenerEntidadPorId(Long id) {
        return clienteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado con ID: " + id));
    }

    @Transactional
    public ClienteDetailDTO crearCliente(ClienteRequestDTO req, String currentUsername) {
        String nifCif = req.getNifCif().trim().toUpperCase();
        if (clienteRepository.existsByNifCif(nifCif)) {
            throw new ConflictException("Ya existe un cliente con el NIF/CIF: " + nifCif);
        }

        String codigo = req.getCodigo();
        if (codigo == null || codigo.isBlank()) {
            codigo = generarCodigoCliente();
        } else {
            codigo = codigo.trim().toUpperCase();
            if (clienteRepository.existsByCodigo(codigo)) {
                throw new ConflictException("Ya existe un cliente con el código: " + codigo);
            }
        }

        Cliente cliente = Cliente.builder()
                .codigo(codigo)
                .nifCif(nifCif)
                .nombre(req.getNombre().trim())
                .estado(req.getEstado() != null && !req.getEstado().isBlank() ? req.getEstado().toUpperCase() : "ALTA")
                .mantenimiento(req.getMantenimiento() != null && !req.getMantenimiento().isBlank() ? req.getMantenimiento() : "ESTANDAR")
                .direccion(req.getDireccion())
                .poblacion(req.getPoblacion())
                .provincia(req.getProvincia())
                .gerente(req.getGerente())
                .fechaAlta(req.getFechaAlta() != null ? req.getFechaAlta() : LocalDate.now())
                .activo(req.getActivo() != null ? req.getActivo() : true)
                .fechaCreacion(LocalDateTime.now())
                .fechaModificacion(LocalDateTime.now())
                .build();

        Cliente guardado = clienteRepository.save(cliente);

        auditoriaService.registrarAsync(
                currentUsername,
                "CLIENTE_CREADO",
                "Cliente",
                guardado.getId(),
                "Cliente creado: " + guardado.getNombre() + " (" + guardado.getCodigo() + ")"
        );

        return ClienteDetailDTO.fromEntity(guardado);
    }

    @Transactional
    public ClienteDetailDTO actualizarCliente(Long id, ClienteRequestDTO req, String currentUsername) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado con ID: " + id));

        List<String> cambios = new ArrayList<>();

        if (req.getNifCif() != null && !req.getNifCif().isBlank()) {
            String nuevoNif = req.getNifCif().trim().toUpperCase();
            if (!nuevoNif.equalsIgnoreCase(cliente.getNifCif())) {
                if (clienteRepository.existsByNifCif(nuevoNif)) {
                    throw new ConflictException("Ya existe otro cliente con el NIF/CIF: " + nuevoNif);
                }
                cambios.add("NIF/CIF: antes '" + cliente.getNifCif() + "' -> después '" + nuevoNif + "'");
                cliente.setNifCif(nuevoNif);
            }
        }

        if (req.getCodigo() != null && !req.getCodigo().isBlank()) {
            String nuevoCodigo = req.getCodigo().trim().toUpperCase();
            if (!nuevoCodigo.equalsIgnoreCase(cliente.getCodigo())) {
                if (clienteRepository.existsByCodigo(nuevoCodigo)) {
                    throw new ConflictException("Ya existe otro cliente con el código: " + nuevoCodigo);
                }
                cambios.add("Código: antes '" + cliente.getCodigo() + "' -> después '" + nuevoCodigo + "'");
                cliente.setCodigo(nuevoCodigo);
            }
        }

        if (req.getNombre() != null && !req.getNombre().isBlank()) {
            String nuevoNombre = req.getNombre().trim();
            if (!nuevoNombre.equals(cliente.getNombre())) {
                cambios.add("Nombre: antes '" + cliente.getNombre() + "' -> después '" + nuevoNombre + "'");
                cliente.setNombre(nuevoNombre);
            }
        }

        if (req.getEstado() != null && !req.getEstado().isBlank()) {
            String nuevoEstado = req.getEstado().toUpperCase();
            if (!nuevoEstado.equals(cliente.getEstado())) {
                cambios.add("Estado: antes '" + cliente.getEstado() + "' -> después '" + nuevoEstado + "'");
                cliente.setEstado(nuevoEstado);
                if ("ALTA".equalsIgnoreCase(nuevoEstado)) {
                    cliente.setActivo(true);
                    cliente.setFechaEliminacion(null);
                    cliente.setEliminadoPor(null);
                } else if ("BAJA".equalsIgnoreCase(nuevoEstado)) {
                    cliente.setActivo(false);
                    cliente.setFechaEliminacion(LocalDateTime.now());
                    usuarioRepository.findByUsername(currentUsername).ifPresent(u -> cliente.setEliminadoPor(u.getId()));
                }
            }
        }

        if (req.getMantenimiento() != null && !Objects.equals(req.getMantenimiento(), cliente.getMantenimiento())) {
            cambios.add("Mantenimiento: antes '" + cliente.getMantenimiento() + "' -> después '" + req.getMantenimiento() + "'");
            cliente.setMantenimiento(req.getMantenimiento());
        }

        if (req.getDireccion() != null && !Objects.equals(req.getDireccion(), cliente.getDireccion())) {
            cambios.add("Dirección: antes '" + cliente.getDireccion() + "' -> después '" + req.getDireccion() + "'");
            cliente.setDireccion(req.getDireccion());
        }

        if (req.getPoblacion() != null && !Objects.equals(req.getPoblacion(), cliente.getPoblacion())) {
            cambios.add("Población: antes '" + cliente.getPoblacion() + "' -> después '" + req.getPoblacion() + "'");
            cliente.setPoblacion(req.getPoblacion());
        }

        if (req.getProvincia() != null && !Objects.equals(req.getProvincia(), cliente.getProvincia())) {
            cambios.add("Provincia: antes '" + cliente.getProvincia() + "' -> después '" + req.getProvincia() + "'");
            cliente.setProvincia(req.getProvincia());
        }

        if (req.getGerente() != null && !Objects.equals(req.getGerente(), cliente.getGerente())) {
            cambios.add("Gerente: antes '" + cliente.getGerente() + "' -> después '" + req.getGerente() + "'");
            cliente.setGerente(req.getGerente());
        }

        if (req.getFechaAlta() != null && !Objects.equals(req.getFechaAlta(), cliente.getFechaAlta())) {
            cambios.add("Fecha Alta: antes '" + cliente.getFechaAlta() + "' -> después '" + req.getFechaAlta() + "'");
            cliente.setFechaAlta(req.getFechaAlta());
        }

        if (req.getActivo() != null && !Objects.equals(req.getActivo(), cliente.getActivo())) {
            cambios.add("Activo: antes '" + cliente.getActivo() + "' -> después '" + req.getActivo() + "'");
            cliente.setActivo(req.getActivo());
            if (Boolean.FALSE.equals(req.getActivo())) {
                cliente.setEstado("BAJA");
                cliente.setFechaEliminacion(LocalDateTime.now());
                usuarioRepository.findByUsername(currentUsername).ifPresent(u -> cliente.setEliminadoPor(u.getId()));
            } else {
                cliente.setEstado("ALTA");
                cliente.setFechaEliminacion(null);
                cliente.setEliminadoPor(null);
            }
        }

        cliente.setFechaModificacion(LocalDateTime.now());
        Cliente actualizado = clienteRepository.save(cliente);

        String detalleAuditoria = cambios.isEmpty()
                ? "Cliente actualizado sin cambios significativos: " + actualizado.getNombre()
                : "Modificó cliente " + actualizado.getNombre() + ". Cambios: " + String.join(" | ", cambios);

        auditoriaService.registrarAsync(
                currentUsername,
                "CLIENTE_MODIFICADO",
                "Cliente",
                actualizado.getId(),
                detalleAuditoria
        );

        return ClienteDetailDTO.fromEntity(actualizado);
    }

    @Transactional
    public ClienteDetailDTO cambiarEstado(Long id, String nuevoEstado, String currentUsername) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado con ID: " + id));

        String estadoAnterior = cliente.getEstado() != null ? cliente.getEstado() : (Boolean.TRUE.equals(cliente.getActivo()) ? "ALTA" : "BAJA");
        String estadoNormalizado = nuevoEstado != null ? nuevoEstado.trim().toUpperCase() : "ALTA";

        if ("ALTA".equals(estadoNormalizado)) {
            cliente.setEstado("ALTA");
            cliente.setActivo(true);
            cliente.setFechaEliminacion(null);
            cliente.setEliminadoPor(null);
        } else {
            cliente.setEstado("BAJA");
            cliente.setActivo(false);
            cliente.setFechaEliminacion(LocalDateTime.now());
            usuarioRepository.findByUsername(currentUsername).ifPresent(u -> cliente.setEliminadoPor(u.getId()));
        }

        cliente.setFechaModificacion(LocalDateTime.now());
        Cliente guardado = clienteRepository.save(cliente);

        auditoriaService.registrarAsync(
                currentUsername,
                "CLIENTE_ESTADO_MODIFICADO",
                "Cliente",
                guardado.getId(),
                "Cliente " + guardado.getNombre() + " (" + guardado.getCodigo() + ") — " + estadoAnterior + " -> " + estadoNormalizado
        );

        return ClienteDetailDTO.fromEntity(guardado);
    }

    @Transactional
    public ClienteDetailDTO darAltaCliente(Long id, String currentUsername) {
        return cambiarEstado(id, "ALTA", currentUsername);
    }

    @Transactional
    public void desactivarCliente(Long id, String currentUsername) {
        cambiarEstado(id, "BAJA", currentUsername);
    }

    @Transactional(readOnly = true)
    public DashboardStatsDTO obtenerEstadisticasDashboard(String currentUsername) {
        long activos = clienteRepository.countByActivo(true);
        long inactivos = clienteRepository.countByActivo(false);
        long totalClientes = activos + inactivos;
        long totalEquipos = equipoRepository.countByActivo(true);
        long totalServicios = servicioRepository.countByActivo(true);
        long totalWebs = webRepository.countByActivo(true);
        long totalDocumentos = documentoRepository.countByActivo(true);
        long totalUsuarios = usuarioRepository.countByActivo(true);

        Usuario currentUser = currentUsername != null ? usuarioRepository.findByUsername(currentUsername).orElse(null) : null;
        Long currentUserId = currentUser != null ? currentUser.getId() : -1L;

        // Filtrado estricto de eventos por privacidad
        Specification<com.abaxial.portal.events.entity.Evento> specPendientes = (root, query, cb) -> {
            Predicate active = cb.isTrue(root.get("activo"));
            Predicate pending = cb.equal(root.get("estado"), "PENDIENTE");
            Predicate isShared = cb.equal(root.get("visibilidad"), "COMPARTIDO");
            Predicate isOwner = cb.and(
                    cb.isNotNull(root.get("usuario")),
                    cb.equal(root.get("usuario").get("id"), currentUserId)
            );
            return cb.and(active, pending, cb.or(isShared, isOwner));
        };
        long eventosPendientes = eventoRepository.count(specPendientes);

        Specification<com.abaxial.portal.events.entity.Evento> specProximos = (root, query, cb) -> {
            Predicate active = cb.isTrue(root.get("activo"));
            Predicate future = cb.greaterThanOrEqualTo(root.get("fechaInicio"), LocalDateTime.now());
            Predicate isShared = cb.equal(root.get("visibilidad"), "COMPARTIDO");
            Predicate isOwner = cb.and(
                    cb.isNotNull(root.get("usuario")),
                    cb.equal(root.get("usuario").get("id"), currentUserId)
            );
            return cb.and(active, future, cb.or(isShared, isOwner));
        };
        List<EventoDTO> proximosEventos = eventoRepository
                .findAll(specProximos, org.springframework.data.domain.PageRequest.of(0, 6, org.springframework.data.domain.Sort.by(org.springframework.data.domain.Sort.Direction.ASC, "fechaInicio")))
                .getContent().stream().map(EventoDTO::fromEntity).collect(Collectors.toList());

        return DashboardStatsDTO.builder()
                .totalClientes(totalClientes)
                .clientesActivos(activos)
                .clientesInactivos(inactivos)
                .totalEquipos(totalEquipos)
                .totalServicios(totalServicios)
                .totalWebs(totalWebs)
                .totalDocumentos(totalDocumentos)
                .totalUsuarios(totalUsuarios)
                .eventosPendientes(eventosPendientes)
                .proximosEventos(proximosEventos)
                .actividadReciente(auditoriaService.obtenerActividadReciente())
                .build();
    }

    @Transactional(readOnly = true)
    public DashboardStatsDTO obtenerEstadisticasDashboard() {
        return obtenerEstadisticasDashboard(null);
    }

    private String generarCodigoCliente() {
        long count = clienteRepository.count() + 1;
        return String.format("CLI-%04d", count);
    }
}
