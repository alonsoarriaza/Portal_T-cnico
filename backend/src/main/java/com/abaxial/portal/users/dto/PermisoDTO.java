package com.abaxial.portal.users.dto;

import com.abaxial.portal.users.entity.Permiso;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PermisoDTO {
    private Long id;
    private String codigo;
    private String nombre;
    private String categoria;
    private String descripcion;

    public static PermisoDTO fromEntity(Permiso p) {
        return PermisoDTO.builder()
                .id(p.getId())
                .codigo(p.getCodigo())
                .nombre(p.getNombre())
                .categoria(p.getCategoria())
                .descripcion(p.getDescripcion())
                .build();
    }
}
