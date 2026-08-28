package com.abaxial.portal.common.config;

import com.abaxial.portal.common.dto.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/health")
public class HealthController {

    @GetMapping
    public ResponseEntity<ApiResponse<Map<String, Object>>> checkHealth() {
        Map<String, Object> status = new HashMap<>();
        status.put("status", "UP");
        status.put("service", "ABAXIAL PORTAL TÉCNICO API");
        status.put("version", "1.0.0");
        status.put("timestamp", LocalDateTime.now());

        return ResponseEntity.ok(ApiResponse.ok("Servicio operativo", status));
    }
}
