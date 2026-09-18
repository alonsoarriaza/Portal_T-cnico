package com.abaxial.portal.clients.importer.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FieldConflictDTO {
    private String entityType; // CLIENTE, EQUIPO
    private String entityIdentifier; // Codigo o referencia
    private String fieldName; // telefono, direccion, gerente, etc.
    private String existingValue;
    private String importedValue;
    private String message;
}
