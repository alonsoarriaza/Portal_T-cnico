package com.abaxial.portal.users.dto;

import com.abaxial.portal.users.entity.Rol;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;
import java.util.stream.Collectors;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoleDTO {
    private Long id;
    private String codigo;
    private String nombre;
    private String descripcion;
    private boolean activo;
    private Set<String> permisos;

    public static RoleDTO fromEntity(Rol rol) {
        Set<String> permCodes = rol.getPermisos() != null
                ? rol.getPermisos().stream().map(p -> p.getCodigo()).collect(Collectors.toSet())
                : Set.of();

        return RoleDTO.builder()
                .id(rol.getId())
                .codigo(rol.getCodigo())
                .nombre(rol.getNombre())
                .descripcion(rol.getDescripcion())
                .activo(Boolean.TRUE.equals(rol.getActivo()))
                .permisos(permCodes)
                .build();
    }
}
