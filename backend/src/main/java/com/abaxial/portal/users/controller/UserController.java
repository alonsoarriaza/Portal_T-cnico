package com.abaxial.portal.users.controller;

import com.abaxial.portal.common.dto.ApiResponse;
import com.abaxial.portal.common.dto.PaginatedResponse;
import com.abaxial.portal.users.dto.UserCreateRequest;
import com.abaxial.portal.users.dto.UserResponseDTO;
import com.abaxial.portal.users.dto.UserUpdateRequest;
import com.abaxial.portal.users.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping
    @PreAuthorize("hasAuthority('USUARIO_VER') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<PaginatedResponse<UserResponseDTO>>> listarUsuarios(
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "false") boolean includeInactive,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "15") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        PaginatedResponse<UserResponseDTO> resultado = userService.listarUsuarios(search, includeInactive, pageable);
        return ResponseEntity.ok(ApiResponse.ok(resultado));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('USUARIO_VER') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<UserResponseDTO>> obtenerUsuario(@PathVariable Long id) {
        UserResponseDTO usuario = userService.obtenerPorId(id);
        return ResponseEntity.ok(ApiResponse.ok(usuario));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('USUARIO_GESTIONAR') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<UserResponseDTO>> crearUsuario(
            @Valid @RequestBody UserCreateRequest request,
            Authentication auth
    ) {
        UserResponseDTO creado = userService.crearUsuario(request, auth.getName());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Usuario creado exitosamente", creado));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('USUARIO_GESTIONAR') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<UserResponseDTO>> actualizarUsuario(
            @PathVariable Long id,
            @Valid @RequestBody UserUpdateRequest request,
            Authentication auth
    ) {
        UserResponseDTO actualizado = userService.actualizarUsuario(id, request, auth.getName());
        return ResponseEntity.ok(ApiResponse.ok("Usuario actualizado correctamente", actualizado));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('USUARIO_GESTIONAR') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<Void>> desactivarUsuario(
            @PathVariable Long id,
            Authentication auth
    ) {
        userService.desactivarUsuario(id, auth.getName());
        return ResponseEntity.ok(ApiResponse.ok("Usuario desactivado correctamente", null));
    }
}
