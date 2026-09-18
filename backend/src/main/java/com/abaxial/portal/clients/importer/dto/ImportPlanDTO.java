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
public class ImportPlanDTO {
    private int clientsFound;
    private int clientsToCreateCount;
    private int clientsToUpdateCount;
    private int clientsToSkipCount;

    private int equipmentsFound;
    private int equipmentsToCreateCount;
    private int equipmentsToSkipCount;

    private int contactsFound;
    private int contactsToCreateCount;
    private int contactsToSkipCount;

    @Builder.Default
    private List<ParsedClient> clientsToCreate = new ArrayList<>();

    @Builder.Default
    private List<ClientUpdatePreviewDTO> clientsToUpdate = new ArrayList<>();

    @Builder.Default
    private List<String> clientsToSkip = new ArrayList<>();

    @Builder.Default
    private List<ParsedEquipment> equipmentsToCreate = new ArrayList<>();

    @Builder.Default
    private List<String> equipmentsToSkip = new ArrayList<>();

    @Builder.Default
    private List<ParsedContact> contactsToCreate = new ArrayList<>();

    @Builder.Default
    private List<String> contactsToSkip = new ArrayList<>();

    @Builder.Default
    private List<FieldConflictDTO> conflicts = new ArrayList<>();

    @Builder.Default
    private List<EquipmentAssociationConflictDTO> associationConflicts = new ArrayList<>();

    @Builder.Default
    private List<String> warnings = new ArrayList<>();

    @Builder.Default
    private List<String> errors = new ArrayList<>();
}
