package com.abaxial.portal.websites.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WebRequestDTO {

    private Long clienteId;

    @NotBlank(message = "El nombre de la web es obligatorio")
    private String nombre;

    @NotBlank(message = "La URL es obligatoria")
    @Pattern(
        regexp = "^(https?://)?([a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,}(/.*)?$",
        message = "El formato de la URL no es válido (debe ser un dominio o URL completa como https://ejemplo.com)"
    )
    private String url;

    private String estado;
    private String descripcion;
    private LocalDate fechaRegistro;
    private String observaciones;
    private Boolean activo;
}
