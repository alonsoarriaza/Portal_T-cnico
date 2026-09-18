package com.abaxial.portal.equipment.dto;

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
public class EquipoRequestDTO {

    private Long clienteId;

    private String codigoInventario;

    @NotBlank(message = "El tipo de equipo es obligatorio")
    private String tipo;

    private String marca;
    private String modelo;
    private String numeroSerie;
    private String nombreEquipo;
    private String ubicacion;
    private String ultimaRevision;
    private String url;
    private String estado;
    private LocalDate fechaAlta;
    private LocalDate fechaBaja;
    private String observaciones;
    private Boolean activo;
}
