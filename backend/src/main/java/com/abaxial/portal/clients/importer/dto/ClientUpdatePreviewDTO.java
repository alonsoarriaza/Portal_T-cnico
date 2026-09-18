package com.abaxial.portal.clients.importer.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClientUpdatePreviewDTO {
    private Long clientId;
    private String codigo;
    private String nombre;
    @Builder.Default
    private Map<String, String> fieldsToFill = new HashMap<>(); // campos que estaban vacíos y se van a rellenar
}
