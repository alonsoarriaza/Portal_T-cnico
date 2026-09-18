package com.abaxial.portal.audit.controller;

import com.abaxial.portal.audit.dto.AuditoriaDTO;
import com.abaxial.portal.audit.service.AuditoriaService;
import com.abaxial.portal.common.dto.ApiResponse;
import com.abaxial.portal.common.dto.PaginatedResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/auditoria")
@RequiredArgsConstructor
public class AuditoriaController {

    private final AuditoriaService auditoriaService;

    @GetMapping
    @PreAuthorize("hasAuthority('HISTORIAL_VER') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<PaginatedResponse<AuditoriaDTO>>> listarAuditorias(
            @RequestParam(required = false) Long usuarioId,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String accion,
            @RequestParam(required = false) String entidad,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime desde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime hasta,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "15") String size
    ) {
        boolean isAll = "ALL".equalsIgnoreCase(size) || "TODOS".equalsIgnoreCase(size) || "-1".equals(size);
        Pageable pageable;
        if (isAll) {
            pageable = PageRequest.of(0, 100_000, Sort.by(Sort.Direction.DESC, "fecha"));
        } else {
            int pageSize = 15;
            try { pageSize = Integer.parseInt(size); if (pageSize <= 0) pageSize = 15; } catch (NumberFormatException e) { pageSize = 15; }
            pageable = PageRequest.of(page, pageSize, Sort.by(Sort.Direction.DESC, "fecha"));
        }
        PaginatedResponse<AuditoriaDTO> resultado = auditoriaService.listarAuditorias(
                usuarioId, search, accion, entidad, desde, hasta, pageable
        );
        if (isAll) {
            resultado.setSize((int) resultado.getTotalElements());
            resultado.setPage(0);
            resultado.setTotalPages(resultado.getTotalElements() > 0 ? 1 : 0);
        }
        return ResponseEntity.ok(ApiResponse.ok(resultado));
    }

    @GetMapping("/recientes")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<List<AuditoriaDTO>>> obtenerActividadReciente() {
        List<AuditoriaDTO> recientes = auditoriaService.obtenerActividadReciente();
        return ResponseEntity.ok(ApiResponse.ok(recientes));
    }
}
