package com.abaxial.portal.services.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ServicioRequestDTO {

    private Long clienteId;

    @NotBlank(message = "El nombre del servicio es obligatorio")
    private String nombre;

    private String descripcion;
    private String estado;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private String observaciones;
    private Boolean activo;
}
