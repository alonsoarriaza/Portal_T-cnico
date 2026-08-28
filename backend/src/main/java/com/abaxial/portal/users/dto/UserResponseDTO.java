package com.abaxial.portal.users.dto;

import com.abaxial.portal.users.entity.Usuario;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponseDTO {
    private Long id;
    private String username;
    private String email;
    private String nombre;
    private String apellidos;
    private String nombreCompleto;
    private boolean activo;
    private LocalDateTime fechaAlta;
    private LocalDateTime fechaModificacion;
    private Set<String> roles;
    private Set<String> permisos;

    public static UserResponseDTO fromEntity(Usuario u) {
        Set<String> roleCodes = u.getRoles() != null
                ? u.getRoles().stream().map(r -> r.getCodigo()).collect(Collectors.toSet())
                : Set.of();

        Set<String> permisoCodes = u.getRoles() != null
                ? u.getRoles().stream()
                    .flatMap(r -> r.getPermisos().stream())
                    .map(p -> p.getCodigo())
                    .collect(Collectors.toSet())
                : Set.of();

        return UserResponseDTO.builder()
                .id(u.getId())
                .username(u.getUsername())
                .email(u.getEmail())
                .nombre(u.getNombre())
                .apellidos(u.getApellidos())
                .nombreCompleto(u.getNombreCompleto())
                .activo(Boolean.TRUE.equals(u.getActivo()))
                .fechaAlta(u.getFechaAlta())
                .fechaModificacion(u.getFechaModificacion())
                .roles(roleCodes)
                .permisos(permisoCodes)
                .build();
    }
}
