package com.abaxial.portal.clients.importer.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ParsedContact {
    private String nombre;
    private String apellidos;
    private String cargo;
    private String email;
    private String telefono;
    private String telefonoFijo;
    private String observaciones;
    private String clientCodigo;
    private String clientNombre;
}
