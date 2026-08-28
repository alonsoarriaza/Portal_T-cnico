package com.abaxial.portal.clients.controller;

import com.abaxial.portal.clients.dto.DashboardStatsDTO;
import com.abaxial.portal.clients.service.ClienteService;
import com.abaxial.portal.common.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final ClienteService clienteService;

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<DashboardStatsDTO>> getDashboardStats(Authentication auth) {
        String username = auth != null ? auth.getName() : null;
        DashboardStatsDTO stats = clienteService.obtenerEstadisticasDashboard(username);
        return ResponseEntity.ok(ApiResponse.ok(stats));
    }

    @GetMapping("/stats")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<DashboardStatsDTO>> getDashboardStatsAlt(Authentication auth) {
        String username = auth != null ? auth.getName() : null;
        DashboardStatsDTO stats = clienteService.obtenerEstadisticasDashboard(username);
        return ResponseEntity.ok(ApiResponse.ok(stats));
    }
}
