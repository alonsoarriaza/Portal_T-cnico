package com.abaxial.portal.services.dto;

import com.abaxial.portal.services.entity.Servicio;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ServicioDTO {
    private Long id;
    private Long clienteId;
    private String clienteNombre;
    private String nombre;
    private String descripcion;
    private String estado;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private String observaciones;
    private boolean activo;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaModificacion;

    public static ServicioDTO fromEntity(Servicio s) {
        return ServicioDTO.builder()
                .id(s.getId())
                .clienteId(s.getCliente() != null ? s.getCliente().getId() : null)
                .clienteNombre(s.getCliente() != null ? s.getCliente().getNombre() : null)
                .nombre(s.getNombre())
                .descripcion(s.getDescripcion())
                .estado(s.getEstado())
                .fechaInicio(s.getFechaInicio())
                .fechaFin(s.getFechaFin())
                .observaciones(s.getObservaciones())
                .activo(Boolean.TRUE.equals(s.getActivo()))
                .fechaCreacion(s.getFechaCreacion())
                .fechaModificacion(s.getFechaModificacion())
                .build();
    }
}
