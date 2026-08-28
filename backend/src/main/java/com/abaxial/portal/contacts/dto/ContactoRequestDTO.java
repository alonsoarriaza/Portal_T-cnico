package com.abaxial.portal.contacts.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContactoRequestDTO {

    private Long clienteId;

    @NotBlank(message = "El nombre del contacto es obligatorio")
    private String nombre;

    private String apellidos;
    private String cargo;

    @Email(message = "Formato de email inválido")
    private String email;

    private String telefono;
    private String telefonoFijo;
    private String observaciones;
    private Boolean activo;
}
