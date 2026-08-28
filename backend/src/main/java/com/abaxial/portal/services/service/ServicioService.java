package com.abaxial.portal.services.service;

import com.abaxial.portal.audit.service.AuditoriaService;
import com.abaxial.portal.clients.entity.Cliente;
import com.abaxial.portal.clients.repository.ClienteRepository;
import com.abaxial.portal.common.dto.PaginatedResponse;
import com.abaxial.portal.common.exception.ResourceNotFoundException;
import com.abaxial.portal.services.dto.ServicioDTO;
import com.abaxial.portal.services.dto.ServicioRequestDTO;
import com.abaxial.portal.services.entity.Servicio;
import com.abaxial.portal.services.repository.ServicioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ServicioService {

    private final ServicioRepository servicioRepository;
    private final ClienteRepository clienteRepository;
    private final AuditoriaService auditoriaService;

    @Transactional(readOnly = true)
    public PaginatedResponse<ServicioDTO> listarServicios(
            Long clienteId, String estado, String search,
            boolean includeInactive, Pageable pageable
    ) {
        Page<Servicio> page = servicioRepository.findAllFiltered(clienteId, estado, search, includeInactive, pageable);
        return PaginatedResponse.from(page.map(ServicioDTO::fromEntity));
    }

    @Transactional(readOnly = true)
    public List<ServicioDTO> listarPorCliente(Long clienteId, boolean soloActivos) {
        List<Servicio> list = soloActivos
                ? servicioRepository.findByClienteIdAndActivoTrueOrderByNombreAsc(clienteId)
                : servicioRepository.findByClienteIdOrderByNombreAsc(clienteId);
        return list.stream().map(ServicioDTO::fromEntity).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ServicioDTO obtenerPorId(Long id) {
        Servicio s = servicioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Servicio no encontrado con ID: " + id));
        return ServicioDTO.fromEntity(s);
    }

    @Transactional
    public ServicioDTO crearServicio(Long clienteId, ServicioRequestDTO req, String currentUsername) {
        Cliente cliente = clienteRepository.findById(clienteId)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado con ID: " + clienteId));

        Servicio servicio = Servicio.builder()
                .cliente(cliente)
                .nombre(req.getNombre().trim())
                .descripcion(req.getDescripcion())
                .estado(req.getEstado() != null && !req.getEstado().isBlank() ? req.getEstado().toUpperCase() : "ACTIVO")
                .fechaInicio(req.getFechaInicio())
                .fechaFin(req.getFechaFin())
                .observaciones(req.getObservaciones())
                .activo(req.getActivo() != null ? req.getActivo() : true)
                .fechaCreacion(LocalDateTime.now())
                .fechaModificacion(LocalDateTime.now())
                .build();

        Servicio guardado = servicioRepository.save(servicio);

        auditoriaService.registrarAsync(
                currentUsername,
                "SERVICIO_CREADO",
                "Servicio",
                guardado.getId(),
                "Servicio creado: " + guardado.getNombre() + " para Cliente " + cliente.getNombre()
        );

        return ServicioDTO.fromEntity(guardado);
    }

    @Transactional
    public ServicioDTO actualizarServicio(Long id, ServicioRequestDTO req, String currentUsername) {
        Servicio servicio = servicioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Servicio no encontrado con ID: " + id));

        if (req.getNombre() != null && !req.getNombre().isBlank()) {
            servicio.setNombre(req.getNombre().trim());
        }
        if (req.getDescripcion() != null) {
            servicio.setDescripcion(req.getDescripcion());
        }
        if (req.getEstado() != null && !req.getEstado().isBlank()) {
            servicio.setEstado(req.getEstado().toUpperCase());
        }
        if (req.getFechaInicio() != null) {
            servicio.setFechaInicio(req.getFechaInicio());
        }
        if (req.getFechaFin() != null) {
            servicio.setFechaFin(req.getFechaFin());
        }
        if (req.getObservaciones() != null) {
            servicio.setObservaciones(req.getObservaciones());
        }
        if (req.getActivo() != null) {
            servicio.setActivo(req.getActivo());
        }

        servicio.setFechaModificacion(LocalDateTime.now());
        Servicio actualizado = servicioRepository.save(servicio);

        auditoriaService.registrarAsync(
                currentUsername,
                "SERVICIO_MODIFICADO",
                "Servicio",
                actualizado.getId(),
                "Servicio modificado: " + actualizado.getNombre()
        );

        return ServicioDTO.fromEntity(actualizado);
    }

    @Transactional
    public void desactivarServicio(Long id, String currentUsername) {
        Servicio servicio = servicioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Servicio no encontrado con ID: " + id));

        servicio.setActivo(false);
        servicio.setEstado("INACTIVO");
        servicio.setFechaModificacion(LocalDateTime.now());
        servicioRepository.save(servicio);

        auditoriaService.registrarAsync(
                currentUsername,
                "SERVICIO_DESACTIVADO",
                "Servicio",
                servicio.getId(),
                "Servicio desactivado: " + servicio.getNombre()
        );
    }
}
