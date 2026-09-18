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
public class ImportSummaryDTO {
    private int clientsFound;
    private int clientsCreated;
    private int clientsMatched;
    private int clientsUpdated;

    private int equipmentsFound;
    private int equipmentsCreated;
    private int equipmentsMatched;
    private int equipmentsSkipped;

    private int contactsFound;
    private int contactsCreated;
    private int contactsMatched;
    private int contactsSkipped;

    private int conflictsCount;
    private int errorsCount;

    @Builder.Default
    private List<FieldConflictDTO> conflicts = new ArrayList<>();

    @Builder.Default
    private List<EquipmentAssociationConflictDTO> associationConflicts = new ArrayList<>();

    @Builder.Default
    private List<String> warnings = new ArrayList<>();

    @Builder.Default
    private List<String> errors = new ArrayList<>();

    @Builder.Default
    private List<String> details = new ArrayList<>();
}
