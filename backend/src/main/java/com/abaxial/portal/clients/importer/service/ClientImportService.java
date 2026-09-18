package com.abaxial.portal.clients.importer.service;

import com.abaxial.portal.audit.service.AuditoriaService;
import com.abaxial.portal.clients.entity.Cliente;
import com.abaxial.portal.clients.importer.dto.*;
import com.abaxial.portal.clients.importer.engine.MatchingEngine;
import com.abaxial.portal.clients.importer.normalizer.DataNormalizer;
import com.abaxial.portal.clients.importer.parser.DocumentParser;
import com.abaxial.portal.clients.importer.parser.DocumentParserFactory;
import com.abaxial.portal.clients.repository.ClienteRepository;
import com.abaxial.portal.contacts.entity.Contacto;
import com.abaxial.portal.contacts.repository.ContactoRepository;
import com.abaxial.portal.equipment.entity.Equipo;
import com.abaxial.portal.equipment.repository.EquipoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class ClientImportService {

    private final DocumentParserFactory parserFactory;
    private final MatchingEngine matchingEngine;
    private final ClienteRepository clienteRepository;
    private final EquipoRepository equipoRepository;
    private final ContactoRepository contactoRepository;
    private final AuditoriaService auditoriaService;

    @Transactional(readOnly = true)
    public ImportPlanDTO analizar(MultipartFile file) {
        log.info("Iniciando análisis de documento para importación: {}", file.getOriginalFilename());
        ParsedImportData data = parseFile(file);
        ImportPlanDTO plan = matchingEngine.createImportPlan(data);
        log.info("Análisis completado: {} clientes encontrados, {} equipos encontrados", plan.getClientsFound(), plan.getEquipmentsFound());
        return plan;
    }

    @Transactional(rollbackFor = Exception.class)
    public ImportSummaryDTO ejecutar(MultipartFile file, String currentUsername) {
        log.info("Ejecutando importación transaccional iniciada por: {}", currentUsername);
        ParsedImportData data = parseFile(file);
        ImportPlanDTO plan = matchingEngine.createImportPlan(data);

        ImportSummaryDTO summary = new ImportSummaryDTO();
        summary.setClientsFound(plan.getClientsFound());
        summary.setEquipmentsFound(plan.getEquipmentsFound());
        summary.setConflicts(plan.getConflicts());
        summary.setAssociationConflicts(plan.getAssociationConflicts());
        summary.setConflictsCount(plan.getConflicts().size() + plan.getAssociationConflicts().size());
        summary.setWarnings(plan.getWarnings());
        summary.setErrors(plan.getErrors());

        // Cache local para resolución de clientes
        Map<String, Cliente> clientsByCode = new HashMap<>();
        Map<String, Cliente> clientsByNormalizedName = new HashMap<>();

        for (Cliente c : clienteRepository.findAll()) {
            if (c.getCodigo() != null) clientsByCode.put(c.getCodigo().trim().toUpperCase(), c);
            String norm = DataNormalizer.normalizeName(c.getNombre());
            if (!norm.isEmpty()) clientsByNormalizedName.put(norm, c);
        }

        int clientsCreated = 0;
        int clientsUpdated = 0;
        int clientsMatched = 0;

        // 1. Crear clientes nuevos
        for (ParsedClient pc : plan.getClientsToCreate()) {
            String codigo = pc.getCodigo();
            if (codigo == null || codigo.isBlank()) {
                codigo = "CLI-" + System.currentTimeMillis() + "-" + (int)(Math.random() * 1000);
            } else {
                codigo = codigo.trim().toUpperCase();
            }

            LocalDate fechaAlta = parseDate(pc.getFechaAltaStr());

            Cliente nuevoCliente = Cliente.builder()
                    .codigo(codigo)
                    .nifCif(pc.getNifCif() != null && !pc.getNifCif().isBlank() ? pc.getNifCif().trim().toUpperCase() : "SIN_CIF")
                    .nombre(pc.getNombre() != null ? pc.getNombre().trim() : "Cliente " + codigo)
                    .estado(pc.getEstado() != null && !pc.getEstado().isBlank() ? pc.getEstado().toUpperCase() : "ALTA")
                    .mantenimiento(pc.getMantenimiento() != null && !pc.getMantenimiento().isBlank() ? pc.getMantenimiento() : "ESTANDAR")
                    .direccion(pc.getDireccion())
                    .poblacion(pc.getPoblacion())
                    .provincia(pc.getProvincia())
                    .gerente(pc.getGerente())
                    .fechaAlta(fechaAlta != null ? fechaAlta : LocalDate.now())
                    .activo(true)
                    .fechaCreacion(LocalDateTime.now())
                    .fechaModificacion(LocalDateTime.now())
                    .build();

            Cliente guardado = clienteRepository.save(nuevoCliente);
            clientsCreated++;
            clientsByCode.put(guardado.getCodigo().toUpperCase(), guardado);
            clientsByNormalizedName.put(DataNormalizer.normalizeName(guardado.getNombre()), guardado);
        }

        // 2. Actualizar clientes existentes (completar datos vacíos con protección estricta)
        for (ClientUpdatePreviewDTO upd : plan.getClientsToUpdate()) {
            Optional<Cliente> opt = clienteRepository.findById(upd.getClientId());
            if (opt.isPresent()) {
                Cliente c = opt.get();
                boolean modified = false;

                if (upd.getFieldsToFill().containsKey("direccion") && (c.getDireccion() == null || c.getDireccion().isBlank())) {
                    c.setDireccion(upd.getFieldsToFill().get("direccion"));
                    modified = true;
                }
                if (upd.getFieldsToFill().containsKey("poblacion") && (c.getPoblacion() == null || c.getPoblacion().isBlank())) {
                    c.setPoblacion(upd.getFieldsToFill().get("poblacion"));
                    modified = true;
                }
                if (upd.getFieldsToFill().containsKey("provincia") && (c.getProvincia() == null || c.getProvincia().isBlank())) {
                    c.setProvincia(upd.getFieldsToFill().get("provincia"));
                    modified = true;
                }
                if (upd.getFieldsToFill().containsKey("gerente") && (c.getGerente() == null || c.getGerente().isBlank())) {
                    c.setGerente(upd.getFieldsToFill().get("gerente"));
                    modified = true;
                }

                if (modified) {
                    c.setFechaModificacion(LocalDateTime.now());
                    clienteRepository.save(c);
                    clientsUpdated++;
                }
                clientsMatched++;
            }
        }

        clientsMatched += plan.getClientsToSkip().size();

        // 3. Crear contactos nuevos (para clientes nuevos y existentes)
        int contactsCreated = 0;
        int contactsMatched = plan.getContactsToSkip().size();

        for (ParsedContact pc : plan.getContactsToCreate()) {
            Cliente targetClient = null;
            if (pc.getClientCodigo() != null) {
                targetClient = clientsByCode.get(pc.getClientCodigo().trim().toUpperCase());
            }
            if (targetClient == null && pc.getClientNombre() != null) {
                targetClient = clientsByNormalizedName.get(DataNormalizer.normalizeName(pc.getClientNombre()));
            }

            if (targetClient == null) {
                summary.getWarnings().add("Contacto " + pc.getNombre() + " omitido: cliente no identificado");
                continue;
            }

            Contacto nuevoContacto = Contacto.builder()
                    .cliente(targetClient)
                    .nombre(pc.getNombre() != null && !pc.getNombre().isBlank() ? pc.getNombre().trim() : "Contacto")
                    .apellidos(pc.getApellidos())
                    .cargo(pc.getCargo())
                    .email(pc.getEmail() != null && !pc.getEmail().isBlank() ? pc.getEmail().trim() : null)
                    .telefono(pc.getTelefono() != null && !pc.getTelefono().isBlank() ? pc.getTelefono().trim() : null)
                    .telefonoFijo(pc.getTelefonoFijo())
                    .observaciones(pc.getObservaciones())
                    .activo(true)
                    .fechaCreacion(LocalDateTime.now())
                    .fechaModificacion(LocalDateTime.now())
                    .build();

            contactoRepository.save(nuevoContacto);
            targetClient.getContactos().add(nuevoContacto);
            contactsCreated++;
        }

        // 4. Crear equipos nuevos
        int equipmentsCreated = 0;
        int equipmentsMatched = plan.getEquipmentsToSkip().size();

        for (ParsedEquipment pe : plan.getEquipmentsToCreate()) {
            String ref = pe.getReference() != null ? pe.getReference().trim() : "";
            if (ref.isEmpty()) continue;

            // Verificación defensiva contra duplicados
            if (equipoRepository.existsByCodigoInventario(ref)) {
                equipmentsMatched++;
                continue;
            }

            // Buscar cliente de destino
            Cliente targetClient = null;
            if (pe.getClientCodigo() != null) {
                targetClient = clientsByCode.get(pe.getClientCodigo().trim().toUpperCase());
            }
            if (targetClient == null && pe.getClientName() != null) {
                targetClient = clientsByNormalizedName.get(DataNormalizer.normalizeName(pe.getClientName()));
            }

            if (targetClient == null) {
                // No se puede asociar equipo sin cliente conocido
                summary.getWarnings().add("Equipo " + ref + " (" + pe.getName() + ") omitido: cliente no identificado");
                continue;
            }

            String tipo = (pe.getType() != null && !pe.getType().isBlank()) ? pe.getType().trim() : "Sobremesa";
            String estado = (pe.getStatus() != null && !pe.getStatus().isBlank()) ? pe.getStatus().trim().toUpperCase() : "OPERATIVO";
            if (estado.equalsIgnoreCase("ALTA")) estado = "OPERATIVO";

            Equipo nuevoEquipo = Equipo.builder()
                    .cliente(targetClient)
                    .codigoInventario(ref)
                    .nombreEquipo(pe.getName() != null && !pe.getName().isBlank() ? pe.getName().trim() : ref)
                    .tipo(tipo)
                    .ubicacion(pe.getLocation())
                    .estado(estado)
                    .ultimaRevision(pe.getLastAction())
                    .url(pe.getUrl())
                    .fechaAlta(LocalDate.now())
                    .activo(true)
                    .fechaCreacion(LocalDateTime.now())
                    .fechaModificacion(LocalDateTime.now())
                    .build();

            equipoRepository.save(nuevoEquipo);
            targetClient.getEquipos().add(nuevoEquipo);
            equipmentsCreated++;
        }

        summary.setClientsCreated(clientsCreated);
        summary.setClientsUpdated(clientsUpdated);
        summary.setClientsMatched(clientsMatched);
        summary.setContactsFound(plan.getContactsFound());
        summary.setContactsCreated(contactsCreated);
        summary.setContactsMatched(contactsMatched);
        summary.setContactsSkipped(plan.getContactsToSkip().size());
        summary.setEquipmentsCreated(equipmentsCreated);
        summary.setEquipmentsMatched(equipmentsMatched);
        summary.setEquipmentsSkipped(plan.getEquipmentsToSkip().size());

        auditoriaService.registrarAsync(
                currentUsername,
                "CLIENTES_IMPORTADOS",
                "Cliente",
                null,
                String.format("Importación completada: %d clientes creados, %d actualizados, %d reutilizados | %d contactos creados, %d reutilizados | %d equipos creados, %d reutilizados | %d conflictos",
                        clientsCreated, clientsUpdated, clientsMatched, contactsCreated, contactsMatched, equipmentsCreated, equipmentsMatched, summary.getConflictsCount())
        );

        log.info("Importación finalizada con éxito: {} clientes nuevos, {} clientes reutilizados, {} contactos nuevos, {} contactos reutilizados, {} equipos nuevos, {} equipos reutilizados",
                clientsCreated, clientsMatched, contactsCreated, contactsMatched, equipmentsCreated, equipmentsMatched);

        return summary;
    }

    private ParsedImportData parseFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("El archivo a importar no puede estar vacío");
        }
        String filename = file.getOriginalFilename() != null ? file.getOriginalFilename() : "documento.txt";
        String contentType = file.getContentType();
        DocumentParser parser = parserFactory.getParser(filename, contentType);

        try {
            return parser.parse(file.getInputStream(), filename);
        } catch (Exception e) {
            log.error("Error al procesar el archivo {}", filename, e);
            throw new RuntimeException("Error al procesar el archivo: " + e.getMessage(), e);
        }
    }

    private LocalDate parseDate(String dateStr) {
        if (dateStr == null || dateStr.isBlank()) return null;
        String clean = dateStr.trim();
        List<DateTimeFormatter> formatters = List.of(
                DateTimeFormatter.ofPattern("dd/MM/yyyy"),
                DateTimeFormatter.ofPattern("dd-MM-yyyy"),
                DateTimeFormatter.ofPattern("dd-MM-yy"),
                DateTimeFormatter.ofPattern("yyyy-MM-dd")
        );
        for (DateTimeFormatter dtf : formatters) {
            try {
                return LocalDate.parse(clean, dtf);
            } catch (Exception ignored) {}
        }
        return null;
    }
}
