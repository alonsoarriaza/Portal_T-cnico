package com.abaxial.portal.clients.importer.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EquipmentAssociationConflictDTO {
    private String equipmentReference;
    private String existingClientCodigo;
    private String existingClientNombre;
    private String importedClientCodigo;
    private String importedClientNombre;
    private String message;
}
