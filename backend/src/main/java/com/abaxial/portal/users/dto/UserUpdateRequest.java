package com.abaxial.portal.users.dto;

import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserUpdateRequest {

    @Email(message = "Formato de email inválido")
    private String email;

    private String password;

    private String nombre;

    private String apellidos;

    private Boolean activo;

    private Set<String> roles;
}
