package com.abaxial.portal.clients.importer.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ParsedEquipment {
    private String reference;
    private String name;
    private String type;
    private String location;
    private String status;
    private String lastAction;
    private String url;
    private String clientName;
    private String clientCodigo;
}
