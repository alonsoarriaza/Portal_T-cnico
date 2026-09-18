package com.abaxial.portal.clients.importer.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ParsedImportData {
    @Builder.Default
    private List<ParsedClient> clients = new ArrayList<>();

    @Builder.Default
    private List<ParsedEquipment> otherEquipments = new ArrayList<>();
}
