package com.abaxial.portal.services.controller;

import com.abaxial.portal.common.dto.ApiResponse;
import com.abaxial.portal.common.dto.PaginatedResponse;
import com.abaxial.portal.services.dto.ServicioDTO;
import com.abaxial.portal.services.dto.ServicioRequestDTO;
import com.abaxial.portal.services.service.ServicioService;
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
public class ServicioController {

    private final ServicioService servicioService;

    @GetMapping("/servicios")
    @PreAuthorize("hasAuthority('SERVICIO_VER') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<PaginatedResponse<ServicioDTO>>> listarServicios(
            @RequestParam(required = false) Long clienteId,
            @RequestParam(required = false) String estado,
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "false") boolean includeInactive,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        PaginatedResponse<ServicioDTO> resultado = servicioService.listarServicios(clienteId, estado, search, includeInactive, pageable);
        return ResponseEntity.ok(ApiResponse.ok(resultado));
    }

    @GetMapping("/clientes/{clienteId}/servicios")
    @PreAuthorize("hasAuthority('SERVICIO_VER') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<List<ServicioDTO>>> listarPorCliente(
            @PathVariable Long clienteId,
            @RequestParam(defaultValue = "true") boolean soloActivos
    ) {
        List<ServicioDTO> servicios = servicioService.listarPorCliente(clienteId, soloActivos);
        return ResponseEntity.ok(ApiResponse.ok(servicios));
    }

    @PostMapping("/clientes/{clienteId}/servicios")
    @PreAuthorize("hasAuthority('SERVICIO_GESTIONAR') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<ServicioDTO>> crearServicio(
            @PathVariable Long clienteId,
            @Valid @RequestBody ServicioRequestDTO request,
            Authentication auth
    ) {
        ServicioDTO creado = servicioService.crearServicio(clienteId, request, auth.getName());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Servicio registrado exitosamente", creado));
    }

    @PutMapping("/servicios/{id}")
    @PreAuthorize("hasAuthority('SERVICIO_GESTIONAR') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<ServicioDTO>> actualizarServicio(
            @PathVariable Long id,
            @Valid @RequestBody ServicioRequestDTO request,
            Authentication auth
    ) {
        ServicioDTO actualizado = servicioService.actualizarServicio(id, request, auth.getName());
        return ResponseEntity.ok(ApiResponse.ok("Servicio actualizado correctamente", actualizado));
    }

    @DeleteMapping("/servicios/{id}")
    @PreAuthorize("hasAuthority('SERVICIO_GESTIONAR') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<Void>> desactivarServicio(
            @PathVariable Long id,
            Authentication auth
    ) {
        servicioService.desactivarServicio(id, auth.getName());
        return ResponseEntity.ok(ApiResponse.ok("Servicio desactivado correctamente", null));
    }
}
