package com.abaxial.portal.equipment.service;

import com.abaxial.portal.audit.service.AuditoriaService;
import com.abaxial.portal.clients.entity.Cliente;
import com.abaxial.portal.clients.repository.ClienteRepository;
import com.abaxial.portal.common.dto.PaginatedResponse;
import com.abaxial.portal.common.exception.ConflictException;
import com.abaxial.portal.common.exception.ResourceNotFoundException;
import com.abaxial.portal.equipment.dto.EquipoDTO;
import com.abaxial.portal.equipment.dto.EquipoRequestDTO;
import com.abaxial.portal.equipment.entity.Equipo;
import com.abaxial.portal.equipment.repository.EquipoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EquipoService {

    private final EquipoRepository equipoRepository;
    private final ClienteRepository clienteRepository;
    private final AuditoriaService auditoriaService;

    @Transactional(readOnly = true)
    public PaginatedResponse<EquipoDTO> listarEquipos(
            Long clienteId, String tipo, String estado, String search,
            boolean includeInactive, Pageable pageable
    ) {
        Page<Equipo> page = equipoRepository.findAllFiltered(clienteId, tipo, estado, search, includeInactive, pageable);
        return PaginatedResponse.from(page.map(EquipoDTO::fromEntity));
    }

    @Transactional(readOnly = true)
    public List<EquipoDTO> listarPorCliente(Long clienteId, boolean soloActivos) {
        List<Equipo> list = soloActivos
                ? equipoRepository.findByClienteIdAndActivoTrueOrderByCodigoInventarioAsc(clienteId)
                : equipoRepository.findByClienteIdOrderByCodigoInventarioAsc(clienteId);
        return list.stream().map(EquipoDTO::fromEntity).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public EquipoDTO obtenerPorId(Long id) {
        Equipo e = equipoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Equipo no encontrado con ID: " + id));
        return EquipoDTO.fromEntity(e);
    }

    @Transactional
    public EquipoDTO crearEquipo(Long clienteId, EquipoRequestDTO req, String currentUsername) {
        Cliente cliente = clienteRepository.findById(clienteId)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado con ID: " + clienteId));

        String codInv = req.getCodigoInventario();
        if (codInv == null || codInv.isBlank()) {
            codInv = generarCodigoInventario(cliente);
        } else {
            codInv = codInv.trim().toUpperCase();
            if (equipoRepository.existsByCodigoInventario(codInv)) {
                throw new ConflictException("Ya existe un equipo con el código de inventario: " + codInv);
            }
        }

        Equipo equipo = Equipo.builder()
                .cliente(cliente)
                .codigoInventario(codInv)
                .tipo(req.getTipo().trim())
                .marca(req.getMarca())
                .modelo(req.getModelo())
                .numeroSerie(req.getNumeroSerie())
                .nombreEquipo(req.getNombreEquipo())
                .ubicacion(req.getUbicacion())
                .ultimaRevision(req.getUltimaRevision())
                .url(req.getUrl())
                .estado(req.getEstado() != null && !req.getEstado().isBlank() ? req.getEstado().toUpperCase() : "OPERATIVO")
                .fechaAlta(req.getFechaAlta() != null ? req.getFechaAlta() : LocalDate.now())
                .fechaBaja(req.getFechaBaja())
                .observaciones(req.getObservaciones())
                .activo(req.getActivo() != null ? req.getActivo() : true)
                .fechaCreacion(LocalDateTime.now())
                .fechaModificacion(LocalDateTime.now())
                .build();

        Equipo guardado = equipoRepository.save(equipo);

        auditoriaService.registrarAsync(
                currentUsername,
                "EQUIPO_CREADO",
                "Equipo",
                guardado.getId(),
                "Equipo creado: " + guardado.getCodigoInventario() + " (" + guardado.getTipo() + ") para Cliente " + cliente.getNombre()
        );

        return EquipoDTO.fromEntity(guardado);
    }

    @Transactional
    public EquipoDTO actualizarEquipo(Long id, EquipoRequestDTO req, String currentUsername) {
        Equipo equipo = equipoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Equipo no encontrado con ID: " + id));

        if (req.getCodigoInventario() != null && !req.getCodigoInventario().isBlank()) {
            String codInv = req.getCodigoInventario().trim().toUpperCase();
            if (!codInv.equalsIgnoreCase(equipo.getCodigoInventario()) && equipoRepository.existsByCodigoInventario(codInv)) {
                throw new ConflictException("Ya existe otro equipo con el código de inventario: " + codInv);
            }
            equipo.setCodigoInventario(codInv);
        }

        if (req.getTipo() != null && !req.getTipo().isBlank()) {
            equipo.setTipo(req.getTipo().trim());
        }
        if (req.getMarca() != null) {
            equipo.setMarca(req.getMarca());
        }
        if (req.getModelo() != null) {
            equipo.setModelo(req.getModelo());
        }
        if (req.getNumeroSerie() != null) {
            equipo.setNumeroSerie(req.getNumeroSerie());
        }
        if (req.getNombreEquipo() != null) {
            equipo.setNombreEquipo(req.getNombreEquipo());
        }
        if (req.getUbicacion() != null) {
            equipo.setUbicacion(req.getUbicacion());
        }
        if (req.getUltimaRevision() != null) {
            equipo.setUltimaRevision(req.getUltimaRevision());
        }
        if (req.getUrl() != null) {
            equipo.setUrl(req.getUrl());
        }
        if (req.getEstado() != null && !req.getEstado().isBlank()) {
            equipo.setEstado(req.getEstado().toUpperCase());
        }
        if (req.getFechaAlta() != null) {
            equipo.setFechaAlta(req.getFechaAlta());
        }
        if (req.getFechaBaja() != null) {
            equipo.setFechaBaja(req.getFechaBaja());
        }
        if (req.getObservaciones() != null) {
            equipo.setObservaciones(req.getObservaciones());
        }
        if (req.getActivo() != null) {
            equipo.setActivo(req.getActivo());
        }

        equipo.setFechaModificacion(LocalDateTime.now());
        Equipo actualizado = equipoRepository.save(equipo);

        auditoriaService.registrarAsync(
                currentUsername,
                "EQUIPO_MODIFICADO",
                "Equipo",
                actualizado.getId(),
                "Equipo modificado: " + actualizado.getCodigoInventario()
        );

        return EquipoDTO.fromEntity(actualizado);
    }

    @Transactional
    public EquipoDTO cambiarEstado(Long id, String nuevoEstado, String currentUsername) {
        Equipo equipo = equipoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Equipo no encontrado con ID: " + id));

        String estadoAnterior = equipo.getEstado();
        String estadoNormalizado = nuevoEstado != null ? nuevoEstado.trim().toUpperCase() : "OPERATIVO";

        equipo.setEstado(estadoNormalizado);
        if ("BAJA".equalsIgnoreCase(estadoNormalizado)) {
            equipo.setFechaBaja(LocalDate.now());
        }
        equipo.setFechaModificacion(LocalDateTime.now());
        Equipo actualizado = equipoRepository.save(equipo);

        auditoriaService.registrarAsync(
                currentUsername,
                "EQUIPO_ESTADO_MODIFICADO",
                "Equipo",
                actualizado.getId(),
                "Cambió estado de equipo " + actualizado.getCodigoInventario() + ": antes '" + estadoAnterior + "' -> después '" + estadoNormalizado + "'"
        );

        return EquipoDTO.fromEntity(actualizado);
    }

    @Transactional
    public void desactivarEquipo(Long id, String currentUsername) {
        Equipo equipo = equipoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Equipo no encontrado con ID: " + id));

        equipo.setActivo(false);
        equipo.setEstado("BAJA");
        equipo.setFechaBaja(LocalDate.now());
        equipo.setFechaModificacion(LocalDateTime.now());
        equipoRepository.save(equipo);

        auditoriaService.registrarAsync(
                currentUsername,
                "EQUIPO_DESACTIVADO",
                "Equipo",
                equipo.getId(),
                "Equipo desactivado: " + equipo.getCodigoInventario()
        );
    }

    private String generarCodigoInventario(Cliente cliente) {
        long count = equipoRepository.countByClienteIdAndActivoTrue(cliente.getId()) + 1;
        return String.format("EQ-%s-%03d", cliente.getCodigo(), count);
    }
}
