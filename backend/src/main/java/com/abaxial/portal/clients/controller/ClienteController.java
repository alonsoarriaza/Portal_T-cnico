package com.abaxial.portal.clients.controller;

import com.abaxial.portal.clients.dto.ClienteDetailDTO;
import com.abaxial.portal.clients.dto.ClienteListDTO;
import com.abaxial.portal.clients.dto.ClienteRequestDTO;
import com.abaxial.portal.clients.dto.DashboardStatsDTO;
import com.abaxial.portal.clients.service.ClienteService;
import com.abaxial.portal.common.dto.ApiResponse;
import com.abaxial.portal.common.dto.PaginatedResponse;
import com.abaxial.portal.pdf.service.PdfExportService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/clientes")
@RequiredArgsConstructor
public class ClienteController {

    private final ClienteService clienteService;
    private final PdfExportService pdfExportService;

    @GetMapping
    @PreAuthorize("hasAuthority('CLIENTE_VER') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<PaginatedResponse<ClienteListDTO>>> listarClientes(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String estado,
            @RequestParam(required = false) String mantenimiento,
            @RequestParam(required = false) String provincia,
            @RequestParam(defaultValue = "false") boolean includeInactive,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") String size,
            @RequestParam(defaultValue = "nombre") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir
    ) {
        Sort sort = sortDir.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        boolean isAll = "ALL".equalsIgnoreCase(size) || "TODOS".equalsIgnoreCase(size) || "-1".equals(size);
        Pageable pageable;
        if (isAll) {
            pageable = PageRequest.of(0, 100_000, sort);
        } else {
            int pageSize = 10;
            try { pageSize = Integer.parseInt(size); if (pageSize <= 0) pageSize = 10; } catch (NumberFormatException e) { pageSize = 10; }
            pageable = PageRequest.of(page, pageSize, sort);
        }

        PaginatedResponse<ClienteListDTO> resultado = clienteService.listarClientes(
                search, estado, mantenimiento, provincia, includeInactive, pageable
        );
        if (isAll) {
            resultado.setSize((int) resultado.getTotalElements());
            resultado.setPage(0);
            resultado.setTotalPages(resultado.getTotalElements() > 0 ? 1 : 0);
        }
        return ResponseEntity.ok(ApiResponse.ok(resultado));
    }

    @GetMapping("/dashboard/stats")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<DashboardStatsDTO>> obtenerEstadisticasDashboard(Authentication auth) {
        String username = auth != null ? auth.getName() : null;
        DashboardStatsDTO stats = clienteService.obtenerEstadisticasDashboard(username);
        return ResponseEntity.ok(ApiResponse.ok(stats));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('CLIENTE_VER') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<ClienteDetailDTO>> obtenerDetalleCliente(@PathVariable Long id) {
        ClienteDetailDTO cliente = clienteService.obtenerDetalleCliente(id);
        return ResponseEntity.ok(ApiResponse.ok(cliente));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('CLIENTE_CREAR') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<ClienteDetailDTO>> crearCliente(
            @Valid @RequestBody ClienteRequestDTO request,
            Authentication auth
    ) {
        ClienteDetailDTO creado = clienteService.crearCliente(request, auth.getName());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Cliente creado exitosamente", creado));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('CLIENTE_EDITAR') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<ClienteDetailDTO>> actualizarCliente(
            @PathVariable Long id,
            @Valid @RequestBody ClienteRequestDTO request,
            Authentication auth
    ) {
        ClienteDetailDTO actualizado = clienteService.actualizarCliente(id, request, auth.getName());
        return ResponseEntity.ok(ApiResponse.ok("Cliente actualizado correctamente", actualizado));
    }

    @PatchMapping("/{id}/estado")
    @PreAuthorize("hasAuthority('CLIENTE_EDITAR') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<ClienteDetailDTO>> cambiarEstado(
            @PathVariable Long id,
            @RequestParam String estado,
            Authentication auth
    ) {
        ClienteDetailDTO actualizado = clienteService.cambiarEstado(id, estado, auth.getName());
        return ResponseEntity.ok(ApiResponse.ok("Estado del cliente modificado a " + estado, actualizado));
    }

    @PatchMapping("/{id}/alta")
    @PreAuthorize("hasAuthority('CLIENTE_EDITAR') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<ClienteDetailDTO>> darAlta(
            @PathVariable Long id,
            Authentication auth
    ) {
        ClienteDetailDTO actualizado = clienteService.darAltaCliente(id, auth.getName());
        return ResponseEntity.ok(ApiResponse.ok("Cliente dado de alta exitosamente", actualizado));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('CLIENTE_ELIMINAR') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<Void>> desactivarCliente(
            @PathVariable Long id,
            Authentication auth
    ) {
        clienteService.desactivarCliente(id, auth.getName());
        return ResponseEntity.ok(ApiResponse.ok("Cliente desactivado correctamente", null));
    }

    @DeleteMapping("/{id}/permanente")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<Void>> eliminarClientePermanente(
            @PathVariable Long id,
            Authentication auth
    ) {
        clienteService.eliminarClientePermanente(id, auth.getName());
        return ResponseEntity.ok(ApiResponse.ok("Cliente eliminado permanentemente", null));
    }

    @GetMapping("/{id}/pdf")
    @PreAuthorize("hasAuthority('CLIENTE_VER') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<Resource> descargarFichaPdf(@PathVariable Long id) {
        byte[] pdfBytes = pdfExportService.generarFichaClientePdf(id);
        ByteArrayResource resource = new ByteArrayResource(pdfBytes);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=Ficha_Cliente_" + id + ".pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .contentLength(pdfBytes.length)
                .body(resource);
    }
}
