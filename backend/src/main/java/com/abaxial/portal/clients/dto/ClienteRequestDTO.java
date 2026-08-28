package com.abaxial.portal.clients.dto;

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
public class ClienteRequestDTO {

    private String codigo;

    @NotBlank(message = "El NIF/CIF es obligatorio")
    private String nifCif;

    @NotBlank(message = "El nombre de la empresa / cliente es obligatorio")
    private String nombre;

    private String estado;
    private String mantenimiento;
    private String direccion;
    private String poblacion;
    private String provincia;
    private String gerente;
    private LocalDate fechaAlta;
    private Boolean activo;
}
