package com.abaxial.portal.websites.controller;

import com.abaxial.portal.common.dto.ApiResponse;
import com.abaxial.portal.common.dto.PaginatedResponse;
import com.abaxial.portal.websites.dto.WebDTO;
import com.abaxial.portal.websites.dto.WebRequestDTO;
import com.abaxial.portal.websites.service.WebService;
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
public class WebController {

    private final WebService webService;

    @GetMapping("/webs")
    @PreAuthorize("hasAuthority('WEB_VER') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<PaginatedResponse<WebDTO>>> listarWebs(
            @RequestParam(required = false) Long clienteId,
            @RequestParam(required = false) String estado,
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "false") boolean includeInactive,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") String size
    ) {
        boolean isAll = "ALL".equalsIgnoreCase(size) || "TODOS".equalsIgnoreCase(size) || "-1".equals(size);
        Pageable pageable;
        if (isAll) {
            pageable = PageRequest.of(0, 100_000);
        } else {
            int pageSize = 10;
            try { pageSize = Integer.parseInt(size); if (pageSize <= 0) pageSize = 10; } catch (NumberFormatException e) { pageSize = 10; }
            pageable = PageRequest.of(page, pageSize);
        }
        PaginatedResponse<WebDTO> resultado = webService.listarWebs(clienteId, estado, search, includeInactive, pageable);
        if (isAll) {
            resultado.setSize((int) resultado.getTotalElements());
            resultado.setPage(0);
            resultado.setTotalPages(resultado.getTotalElements() > 0 ? 1 : 0);
        }
        return ResponseEntity.ok(ApiResponse.ok(resultado));
    }

    @GetMapping("/clientes/{clienteId}/webs")
    @PreAuthorize("hasAuthority('WEB_VER') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<List<WebDTO>>> listarPorCliente(
            @PathVariable Long clienteId,
            @RequestParam(defaultValue = "true") boolean soloActivos
    ) {
        List<WebDTO> webs = webService.listarPorCliente(clienteId, soloActivos);
        return ResponseEntity.ok(ApiResponse.ok(webs));
    }

    @PostMapping("/clientes/{clienteId}/webs")
    @PreAuthorize("hasAuthority('WEB_GESTIONAR') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<WebDTO>> crearWeb(
            @PathVariable Long clienteId,
            @Valid @RequestBody WebRequestDTO request,
            Authentication auth
    ) {
        WebDTO creada = webService.crearWeb(clienteId, request, auth.getName());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Página Web registrada exitosamente", creada));
    }

    @PutMapping("/webs/{id}")
    @PreAuthorize("hasAuthority('WEB_GESTIONAR') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<WebDTO>> actualizarWeb(
            @PathVariable Long id,
            @Valid @RequestBody WebRequestDTO request,
            Authentication auth
    ) {
        WebDTO actualizada = webService.actualizarWeb(id, request, auth.getName());
        return ResponseEntity.ok(ApiResponse.ok("Página Web actualizada correctamente", actualizada));
    }

    @DeleteMapping("/webs/{id}")
    @PreAuthorize("hasAuthority('WEB_GESTIONAR') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<Void>> desactivarWeb(
            @PathVariable Long id,
            Authentication auth
    ) {
        webService.desactivarWeb(id, auth.getName());
        return ResponseEntity.ok(ApiResponse.ok("Página Web desactivada correctamente", null));
    }
}
