package com.abaxial.portal.clients.importer.engine;

import com.abaxial.portal.clients.entity.Cliente;
import com.abaxial.portal.clients.importer.dto.*;
import com.abaxial.portal.clients.importer.normalizer.DataNormalizer;
import com.abaxial.portal.clients.repository.ClienteRepository;
import com.abaxial.portal.contacts.entity.Contacto;
import com.abaxial.portal.contacts.repository.ContactoRepository;
import com.abaxial.portal.equipment.entity.Equipo;
import com.abaxial.portal.equipment.repository.EquipoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
@RequiredArgsConstructor
public class MatchingEngine {

    private final ClienteRepository clienteRepository;
    private final EquipoRepository equipoRepository;
    private final ContactoRepository contactoRepository;

    public ImportPlanDTO createImportPlan(ParsedImportData data) {
        ImportPlanDTO plan = new ImportPlanDTO();

        // 1. Cargar todos los clientes activos para matching por nombre normalizado (fallback eficiente en memoria)
        List<Cliente> allExistingClients = clienteRepository.findAll();
        Map<String, Cliente> clientsByNormalizedName = new HashMap<>();
        for (Cliente c : allExistingClients) {
            String normName = DataNormalizer.normalizeName(c.getNombre());
            if (!normName.isEmpty() && !clientsByNormalizedName.containsKey(normName)) {
                clientsByNormalizedName.put(normName, c);
            }
        }

        // Cache de equipos para detectar duplicados dentro del mismo documento y frente a BBDD
        Map<String, Long> processedEquipmentsRefToClientId = new HashMap<>();
        Set<String> processedEquipmentRefs = new HashSet<>();
        Set<String> processedClientContacts = new HashSet<>();

        int totalEquipmentsCount = 0;
        int totalContactsCount = 0;

        // Map para asociar clientes parseados con entidades existentes
        Map<ParsedClient, Cliente> matchedClientMap = new HashMap<>();

        // 2. Procesar Clientes de la Sección 1
        plan.setClientsFound(data.getClients().size());

        for (ParsedClient pc : data.getClients()) {
            Cliente existingClient = findExistingClient(pc, clientsByNormalizedName);

            if (existingClient == null) {
                // Cliente NUEVO
                plan.getClientsToCreate().add(pc);
            } else {
                // Cliente EXISTENTE
                matchedClientMap.put(pc, existingClient);
                ClientUpdatePreviewDTO updatePreview = detectClientUpdatesAndConflicts(existingClient, pc, plan.getConflicts());

                if (!updatePreview.getFieldsToFill().isEmpty()) {
                    plan.getClientsToUpdate().add(updatePreview);
                } else {
                    plan.getClientsToSkip().add(existingClient.getCodigo() + " - " + existingClient.getNombre());
                }
            }

            // Procesar contactos anidados en el cliente
            if (pc.getContacts() != null) {
                for (ParsedContact pContact : pc.getContacts()) {
                    totalContactsCount++;
                    processContactInPlan(pContact, existingClient, pc, plan, processedClientContacts);
                }
            }

            // Procesar equipos anidados en el cliente
            if (pc.getEquipments() != null) {
                for (ParsedEquipment pe : pc.getEquipments()) {
                    totalEquipmentsCount++;
                    processEquipmentInPlan(pe, existingClient, pc, plan, processedEquipmentRefs, processedEquipmentsRefToClientId);
                }
            }
        }

        // 3. Procesar Equipos de la Sección 2 (Otros Dispositivos / Tabla global)
        if (data.getOtherEquipments() != null) {
            for (ParsedEquipment pe : data.getOtherEquipments()) {
                totalEquipmentsCount++;

                // Buscar cliente asociado por nombre
                Cliente associatedClient = null;
                if (pe.getClientName() != null && !pe.getClientName().isBlank()) {
                    String normClientName = DataNormalizer.normalizeName(pe.getClientName());
                    associatedClient = clientsByNormalizedName.get(normClientName);
                }

                processEquipmentInPlan(pe, associatedClient, null, plan, processedEquipmentRefs, processedEquipmentsRefToClientId);
            }
        }

        plan.setEquipmentsFound(totalEquipmentsCount);
        plan.setContactsFound(totalContactsCount);
        plan.setClientsToCreateCount(plan.getClientsToCreate().size());
        plan.setClientsToUpdateCount(plan.getClientsToUpdate().size());
        plan.setClientsToSkipCount(plan.getClientsToSkip().size());
        plan.setEquipmentsToCreateCount(plan.getEquipmentsToCreate().size());
        plan.setEquipmentsToSkipCount(plan.getEquipmentsToSkip().size());
        plan.setContactsToCreateCount(plan.getContactsToCreate().size());
        plan.setContactsToSkipCount(plan.getContactsToSkip().size());

        return plan;
    }

    public Cliente findExistingClient(ParsedClient pc, Map<String, Cliente> clientsByNormalizedName) {
        // Nivel 1: Código exacto
        if (pc.getCodigo() != null && !pc.getCodigo().isBlank()) {
            Optional<Cliente> byCodigo = clienteRepository.findByCodigoIgnoreCase(pc.getCodigo().trim());
            if (byCodigo.isPresent()) return byCodigo.get();
        }

        // Nivel 2: CIF/NIF exacto (solo si coincide de forma inequívoca)
        if (pc.getNifCif() != null && !pc.getNifCif().isBlank()) {
            List<Cliente> byNif = clienteRepository.findByNifCifIgnoreCase(pc.getNifCif().trim());
            if (byNif.size() == 1) {
                return byNif.get(0);
            }
        }

        // Nivel 3: Nombre normalizado como fallback
        if (pc.getNombre() != null && !pc.getNombre().isBlank()) {
            String norm = DataNormalizer.normalizeName(pc.getNombre());
            if (clientsByNormalizedName != null && clientsByNormalizedName.containsKey(norm)) {
                return clientsByNormalizedName.get(norm);
            }
        }

        return null;
    }

    private ClientUpdatePreviewDTO detectClientUpdatesAndConflicts(
            Cliente existing, ParsedClient imported, List<FieldConflictDTO> conflicts
    ) {
        ClientUpdatePreviewDTO updatePreview = ClientUpdatePreviewDTO.builder()
                .clientId(existing.getId())
                .codigo(existing.getCodigo())
                .nombre(existing.getNombre())
                .build();

        // Comparar campo a campo: Dirección, Población, Provincia, Gerente
        checkField(existing.getCodigo(), "direccion", existing.getDireccion(), imported.getDireccion(), updatePreview, conflicts);
        checkField(existing.getCodigo(), "poblacion", existing.getPoblacion(), imported.getPoblacion(), updatePreview, conflicts);
        checkField(existing.getCodigo(), "provincia", existing.getProvincia(), imported.getProvincia(), updatePreview, conflicts);
        checkField(existing.getCodigo(), "gerente", existing.getGerente(), imported.getGerente(), updatePreview, conflicts);

        return updatePreview;
    }

    private void checkField(
            String clientCodigo, String fieldName, String existingVal, String importedVal,
            ClientUpdatePreviewDTO updatePreview, List<FieldConflictDTO> conflicts
    ) {
        boolean existingHasValue = existingVal != null && !existingVal.trim().isEmpty();
        boolean importedHasValue = importedVal != null && !importedVal.trim().isEmpty();

        if (existingHasValue && importedHasValue) {
            String normExist = DataNormalizer.normalizeName(existingVal);
            String normImport = DataNormalizer.normalizeName(importedVal);
            if (!normExist.equals(normImport)) {
                // Conflicto detectado: NO SOBRESCRIBIR
                conflicts.add(FieldConflictDTO.builder()
                        .entityType("CLIENTE")
                        .entityIdentifier(clientCodigo)
                        .fieldName(fieldName)
                        .existingValue(existingVal)
                        .importedValue(importedVal)
                        .message("Valor existente (" + existingVal + ") conservado frente a valor importado (" + importedVal + ")")
                        .build());
            }
        } else if (!existingHasValue && importedHasValue) {
            // Valor nuevo que completa información faltante
            updatePreview.getFieldsToFill().put(fieldName, importedVal.trim());
        }
    }

    private void processEquipmentInPlan(
            ParsedEquipment pe, Cliente associatedClient, ParsedClient parsedClientOwner,
            ImportPlanDTO plan, Set<String> processedRefs, Map<String, Long> processedEquipmentsRefToClientId
    ) {
        String rawRef = pe.getReference() != null ? pe.getReference().trim() : "";
        if (rawRef.isEmpty()) return;

        // Regla 14: Si aparece repetido en el mismo documento, reutilizarlo y no duplicarlo
        if (processedRefs.contains(rawRef.toUpperCase())) {
            plan.getEquipmentsToSkip().add(rawRef + " (Duplicado dentro del mismo documento)");
            return;
        }
        processedRefs.add(rawRef.toUpperCase());

        // Regla 13: Buscar si ya existe en la BBDD
        Optional<Equipo> existingEquipoOpt = equipoRepository.findByCodigoInventarioIgnoreCase(rawRef);

        if (existingEquipoOpt.isPresent()) {
            Equipo existingEquipo = existingEquipoOpt.get();
            Cliente actualOwner = existingEquipo.getCliente();

            // Regla 15: Conflicto de asignación de cliente
            if (associatedClient != null && actualOwner != null && !actualOwner.getId().equals(associatedClient.getId())) {
                plan.getAssociationConflicts().add(EquipmentAssociationConflictDTO.builder()
                        .equipmentReference(rawRef)
                        .existingClientCodigo(actualOwner.getCodigo())
                        .existingClientNombre(actualOwner.getNombre())
                        .importedClientCodigo(associatedClient.getCodigo())
                        .importedClientNombre(associatedClient.getNombre())
                        .message("Equipo " + rawRef + " pertenece a Cliente " + actualOwner.getNombre() + " (" + actualOwner.getCodigo() + "). NO se reasigna a " + associatedClient.getNombre())
                        .build());
            }

            // Ya existe en BBDD -> Reutilizar, no insertar
            plan.getEquipmentsToSkip().add(rawRef + " (Ya existe en base de datos para cliente " + (actualOwner != null ? actualOwner.getNombre() : "-") + ")");
            processedEquipmentsRefToClientId.put(rawRef.toUpperCase(), actualOwner != null ? actualOwner.getId() : null);
        } else {
            // Equipo nuevo a crear
            // Si el cliente parseado es nuevo, se asociará al crearlo
            if (associatedClient != null) {
                pe.setClientCodigo(associatedClient.getCodigo());
                pe.setClientName(associatedClient.getNombre());
            } else if (parsedClientOwner != null && parsedClientOwner.getCodigo() != null) {
                pe.setClientCodigo(parsedClientOwner.getCodigo());
                pe.setClientName(parsedClientOwner.getNombre());
            }
            plan.getEquipmentsToCreate().add(pe);
        }
    }

    private void processContactInPlan(
            ParsedContact pc, Cliente associatedClient, ParsedClient parsedClientOwner,
            ImportPlanDTO plan, Set<String> processedClientContacts
    ) {
        String clientIdentifier = associatedClient != null ? associatedClient.getCodigo() : (parsedClientOwner != null ? parsedClientOwner.getCodigo() : "");
        String contactKey = (clientIdentifier != null ? clientIdentifier : "") + "|" + DataNormalizer.normalizeName(pc.getNombre()) + "|" + DataNormalizer.normalizeEmail(pc.getEmail());

        if (processedClientContacts.contains(contactKey)) {
            plan.getContactsToSkip().add(pc.getNombre() + " (Duplicado dentro del mismo documento)");
            return;
        }
        processedClientContacts.add(contactKey);

        if (associatedClient != null) {
            // Buscar contactos existentes en BBDD para este cliente
            List<Contacto> existingContacts = contactoRepository.findByClienteIdOrderByNombreAsc(associatedClient.getId());
            Contacto matched = null;

            // Prioridad 1: Email normalizado
            if (pc.getEmail() != null && !pc.getEmail().isBlank()) {
                String normEmail = DataNormalizer.normalizeEmail(pc.getEmail());
                matched = existingContacts.stream()
                        .filter(c -> c.getEmail() != null && DataNormalizer.normalizeEmail(c.getEmail()).equals(normEmail))
                        .findFirst().orElse(null);
            }

            // Prioridad 2: Nombre normalizado
            if (matched == null && pc.getNombre() != null && !pc.getNombre().isBlank()) {
                String normName = DataNormalizer.normalizeName(pc.getNombre());
                matched = existingContacts.stream()
                        .filter(c -> c.getNombre() != null && DataNormalizer.normalizeName(c.getNombre()).equals(normName))
                        .findFirst().orElse(null);
            }

            // Prioridad 3: Teléfono normalizado
            if (matched == null && pc.getTelefono() != null && !pc.getTelefono().isBlank()) {
                String normPhone = DataNormalizer.normalizePhone(pc.getTelefono());
                matched = existingContacts.stream()
                        .filter(c -> c.getTelefono() != null && DataNormalizer.normalizePhone(c.getTelefono()).equals(normPhone))
                        .findFirst().orElse(null);
            }

            if (matched != null) {
                // Contacto ya existe en BBDD para este cliente -> Comprobar conflictos
                checkContactFieldConflict("email", matched.getEmail(), pc.getEmail(), associatedClient.getCodigo(), matched.getNombre(), plan.getConflicts());
                checkContactFieldConflict("telefono", matched.getTelefono(), pc.getTelefono(), associatedClient.getCodigo(), matched.getNombre(), plan.getConflicts());
                checkContactFieldConflict("cargo", matched.getCargo(), pc.getCargo(), associatedClient.getCodigo(), matched.getNombre(), plan.getConflicts());

                plan.getContactsToSkip().add(matched.getNombre() + " (" + (matched.getCargo() != null ? matched.getCargo() : "Contacto") + ") - Ya existe en cliente " + associatedClient.getCodigo());
                return;
            }
        }

        // Contacto nuevo a crear
        if (associatedClient != null) {
            pc.setClientCodigo(associatedClient.getCodigo());
            pc.setClientNombre(associatedClient.getNombre());
        } else if (parsedClientOwner != null) {
            pc.setClientCodigo(parsedClientOwner.getCodigo());
            pc.setClientNombre(parsedClientOwner.getNombre());
        }
        plan.getContactsToCreate().add(pc);
    }

    private void checkContactFieldConflict(String fieldName, String existingVal, String importedVal, String clientCodigo, String contactNombre, List<FieldConflictDTO> conflicts) {
        boolean existingHasValue = existingVal != null && !existingVal.trim().isEmpty();
        boolean importedHasValue = importedVal != null && !importedVal.trim().isEmpty();

        if (existingHasValue && importedHasValue) {
            String normExist = fieldName.equals("email") ? DataNormalizer.normalizeEmail(existingVal) : DataNormalizer.normalizeName(existingVal);
            String normImport = fieldName.equals("email") ? DataNormalizer.normalizeEmail(importedVal) : DataNormalizer.normalizeName(importedVal);
            if (!normExist.equals(normImport)) {
                conflicts.add(FieldConflictDTO.builder()
                        .entityType("CONTACTO")
                        .entityIdentifier(clientCodigo + " / " + contactNombre)
                        .fieldName(fieldName)
                        .existingValue(existingVal)
                        .importedValue(importedVal)
                        .message("Contacto " + contactNombre + ": Valor existente (" + existingVal + ") conservado frente a valor importado (" + importedVal + ")")
                        .build());
            }
        }
    }
}
