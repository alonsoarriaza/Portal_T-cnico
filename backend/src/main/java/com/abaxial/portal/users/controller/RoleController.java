package com.abaxial.portal.users.controller;

import com.abaxial.portal.common.dto.ApiResponse;
import com.abaxial.portal.users.dto.PermisoDTO;
import com.abaxial.portal.users.dto.RoleDTO;
import com.abaxial.portal.users.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/roles")
@RequiredArgsConstructor
public class RoleController {

    private final UserService userService;

    @GetMapping
    @PreAuthorize("hasAuthority('ROL_GESTIONAR') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<List<RoleDTO>>> listarRoles() {
        List<RoleDTO> roles = userService.listarRoles();
        return ResponseEntity.ok(ApiResponse.ok(roles));
    }

    @GetMapping("/permisos")
    @PreAuthorize("hasAuthority('PERMISO_GESTIONAR') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<List<PermisoDTO>>> listarPermisos() {
        List<PermisoDTO> permisos = userService.listarPermisos();
        return ResponseEntity.ok(ApiResponse.ok(permisos));
    }
}
