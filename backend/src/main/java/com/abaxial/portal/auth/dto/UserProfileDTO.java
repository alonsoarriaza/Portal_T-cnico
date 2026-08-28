package com.abaxial.portal.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileDTO {
    private Long id;
    private String username;
    private String email;
    private String nombre;
    private String apellidos;
    private String nombreCompleto;
    private boolean activo;
    private Set<String> roles;
    private Set<String> permisos;
}
