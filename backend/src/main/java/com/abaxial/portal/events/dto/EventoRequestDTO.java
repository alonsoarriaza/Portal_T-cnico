package com.abaxial.portal.events.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventoRequestDTO {

    private Long clienteId;

    @NotBlank(message = "El título del evento es obligatorio")
    private String titulo;

    private String descripcion;

    @NotNull(message = "La fecha de inicio es obligatoria")
    private LocalDateTime fechaInicio;

    private LocalDateTime fechaFin;
    private String tipo;
    private String recurrencia;
    private LocalDateTime fechaFinRecurrencia;
    private String diasSemana;
    private Integer diaMes;
    private Long eventoPadreId;
    private LocalDateTime fechaOriginalOcurrencia;
    private Boolean esExcepcion;
    private String fechasExcluidas;
    private String prioridad;
    private String estado;
    private String visibilidad;
    private Boolean activo;
}
