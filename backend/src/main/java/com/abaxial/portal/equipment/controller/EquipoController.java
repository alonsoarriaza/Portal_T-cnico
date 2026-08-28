package com.abaxial.portal.equipment.controller;

import com.abaxial.portal.common.dto.ApiResponse;
import com.abaxial.portal.common.dto.PaginatedResponse;
import com.abaxial.portal.equipment.dto.EquipoDTO;
import com.abaxial.portal.equipment.dto.EquipoRequestDTO;
import com.abaxial.portal.equipment.service.EquipoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class EquipoController {

    private final EquipoService equipoService;

    @GetMapping("/equipos")
    @PreAuthorize("hasAuthority('EQUIPO_VER') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<PaginatedResponse<EquipoDTO>>> listarEquipos(
            @RequestParam(required = false) Long clienteId,
            @RequestParam(required = false) String tipo,
            @RequestParam(required = false) String estado,
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "false") boolean includeInactive,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        PaginatedResponse<EquipoDTO> resultado = equipoService.listarEquipos(clienteId, tipo, estado, search, includeInactive, pageable);
        return ResponseEntity.ok(ApiResponse.ok(resultado));
    }

    @GetMapping("/clientes/{clienteId}/equipos")
    @PreAuthorize("hasAuthority('EQUIPO_VER') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<List<EquipoDTO>>> listarPorCliente(
            @PathVariable Long clienteId,
            @RequestParam(defaultValue = "true") boolean soloActivos
    ) {
        List<EquipoDTO> equipos = equipoService.listarPorCliente(clienteId, soloActivos);
        return ResponseEntity.ok(ApiResponse.ok(equipos));
    }

    @PostMapping("/clientes/{clienteId}/equipos")
    @PreAuthorize("hasAuthority('EQUIPO_CREAR') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<EquipoDTO>> crearEquipo(
            @PathVariable Long clienteId,
            @Valid @RequestBody EquipoRequestDTO request,
            Authentication auth
    ) {
        EquipoDTO creado = equipoService.crearEquipo(clienteId, request, auth.getName());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Equipo creado exitosamente", creado));
    }

    @PutMapping("/equipos/{id}")
    @PreAuthorize("hasAuthority('EQUIPO_EDITAR') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<EquipoDTO>> actualizarEquipo(
            @PathVariable Long id,
            @Valid @RequestBody EquipoRequestDTO request,
            Authentication auth
    ) {
        EquipoDTO actualizado = equipoService.actualizarEquipo(id, request, auth.getName());
        return ResponseEntity.ok(ApiResponse.ok("Equipo actualizado correctamente", actualizado));
    }

    @PatchMapping("/equipos/{id}/estado")
    @PreAuthorize("hasAuthority('EQUIPO_EDITAR') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<EquipoDTO>> cambiarEstado(
            @PathVariable Long id,
            @RequestParam String estado,
            Authentication auth
    ) {
        EquipoDTO actualizado = equipoService.cambiarEstado(id, estado, auth.getName());
        return ResponseEntity.ok(ApiResponse.ok("Estado del equipo actualizado correctamente", actualizado));
    }

    @DeleteMapping("/equipos/{id}")
    @PreAuthorize("hasAuthority('EQUIPO_ELIMINAR') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<Void>> desactivarEquipo(
            @PathVariable Long id,
            Authentication auth
    ) {
        equipoService.desactivarEquipo(id, auth.getName());
        return ResponseEntity.ok(ApiResponse.ok("Equipo dado de baja correctamente", null));
    }
}
