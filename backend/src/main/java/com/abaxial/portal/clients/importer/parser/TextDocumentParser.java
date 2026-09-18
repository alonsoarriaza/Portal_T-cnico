package com.abaxial.portal.clients.importer.parser;

import com.abaxial.portal.clients.importer.dto.ParsedClient;
import com.abaxial.portal.clients.importer.dto.ParsedContact;
import com.abaxial.portal.clients.importer.dto.ParsedEquipment;
import com.abaxial.portal.clients.importer.dto.ParsedImportData;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Pattern;

@Component
public class TextDocumentParser implements DocumentParser {

    private static final Set<String> CLIENT_FIELD_NAMES = Set.of(
            "estado", "mantenimiento", "dia mantenimiento", "tecnico que realiza la visita",
            "técnico que realiza la visita", "numero de visitas en el mes", "número de visitas en el mes",
            "equipos/dispositivos inventariados", "webs registradas", "codigo", "código",
            "cif/nif", "cif", "nif", "nombre completo", "razon social", "razón social",
            "direccion", "dirección", "poblacion", "población", "provincia", "gerente",
            "email de gerente", "email gerente", "persona tic", "email persona tic", "email tic",
            "telefono de contacto", "teléfono de contacto", "telefono", "teléfono", "fecha alta",
            "nombre de contacto", "persona de contacto", "contacto", "email", "correo",
            "email de contacto", "correo de contacto", "cargo", "rol", "puesto"
    );

    private static final Pattern EQUIPMENT_REF_PATTERN = Pattern.compile("^(\\d{4,6}-\\d{1,4}|EQ-[A-Za-z0-9_-]+)$");

    @Override
    public boolean supports(String filename, String contentType) {
        if (filename == null) return false;
        String lower = filename.toLowerCase();
        return lower.endsWith(".txt") || lower.endsWith(".csv") ||
               (contentType != null && (contentType.contains("text/plain") || contentType.contains("text/csv")));
    }

    @Override
    public ParsedImportData parse(InputStream inputStream, String filename) {
        List<String> rawLines = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                rawLines.add(line.trim());
            }
        } catch (Exception e) {
            throw new RuntimeException("Error al leer el archivo de texto: " + e.getMessage(), e);
        }

        return parseLines(rawLines);
    }

    public ParsedImportData parseLines(List<String> lines) {
        ParsedImportData result = new ParsedImportData();

        // Detectar corte de sección 2 (Otros dispositivos)
        int section2Index = -1;
        for (int i = 0; i < lines.size(); i++) {
            String l = lines.get(i);
            if (l.contains("CREACION DE UN CAMPO DE OTROS DISPOSITIVO") || l.contains("IMPORTAR EN USUARIOS SUS WEBS")) {
                if (section2Index == -1 && l.contains("CREACION DE UN CAMPO DE OTROS DISPOSITIVO")) {
                    section2Index = i;
                }
            }
        }

        int section1Limit = (section2Index != -1) ? section2Index : lines.size();

        // 1. Parsear Sección 1: Clientes y sus Equipos
        List<ParsedClient> parsedClients = new ArrayList<>();
        Map<String, String> currentFields = new LinkedHashMap<>();
        List<ParsedEquipment> currentEquipments = new ArrayList<>();
        String currentCandidateHeader = "";
        boolean inEquiposBlock = false;

        int i = 0;
        while (i < section1Limit) {
            String line = lines.get(i);
            if (line.isEmpty()) {
                i++;
                continue;
            }

            // Detección de cabecera de equipos
            if (line.equalsIgnoreCase("EQUIPOS") || line.equalsIgnoreCase("EQUIPOS:") || line.equalsIgnoreCase("Equipos:")) {
                inEquiposBlock = true;
                i++;
                continue;
            }

            // Detección de clave-valor
            String key = null;
            String val = null;

            if (line.contains("\t")) {
                int tabIdx = line.indexOf('\t');
                key = line.substring(0, tabIdx).trim();
                val = line.substring(tabIdx + 1).trim();
            } else if (line.contains(":") && !line.matches("^\\d{2}/\\d{2}/\\d{4}.*")) {
                int colonIdx = line.indexOf(':');
                String candidateKey = line.substring(0, colonIdx).trim().toLowerCase();
                if (CLIENT_FIELD_NAMES.contains(candidateKey)) {
                    key = line.substring(0, colonIdx).trim();
                    val = line.substring(colonIdx + 1).trim();
                }
            }

            if (key == null) {
                String candidateKey = line.toLowerCase();
                if (CLIENT_FIELD_NAMES.contains(candidateKey)) {
                    key = line;
                    if (i + 1 < section1Limit && !isKeyOrHeader(lines.get(i + 1))) {
                        val = lines.get(i + 1);
                        i++;
                    } else {
                        val = "";
                    }
                }
            }

            if (key != null) {
                String cleanKey = key.toLowerCase().replace(":", "").trim();

                // ¿Indica inicio de nuevo cliente?
                // Si estábamos en bloque de equipos y encontramos campos de cliente, o si encontramos 'código' repetido
                boolean isNewClientBoundary = (inEquiposBlock && (cleanKey.equals("estado") || cleanKey.equals("código") || cleanKey.equals("codigo") || cleanKey.equals("mantenimiento")))
                        || ((cleanKey.equals("código") || cleanKey.equals("codigo")) && (currentFields.containsKey("código") || currentFields.containsKey("codigo")));

                if (isNewClientBoundary && (!currentFields.isEmpty() || !currentCandidateHeader.isEmpty())) {
                    ParsedClient finishedClient = buildClient(currentFields, currentCandidateHeader, currentEquipments);
                    if (finishedClient != null) {
                        parsedClients.add(finishedClient);
                    }
                    currentFields = new LinkedHashMap<>();
                    currentEquipments = new ArrayList<>();
                    currentCandidateHeader = "";
                    inEquiposBlock = false;
                }

                currentFields.put(cleanKey, val);
                i++;
                continue;
            }

            // Si estamos en bloque de equipos y encontramos una referencia
            if (inEquiposBlock && isEquipmentReference(line)) {
                String ref = line;
                String name = (i + 1 < section1Limit) ? lines.get(i + 1) : "";
                String type = (i + 2 < section1Limit) ? lines.get(i + 2) : "";
                String loc = (i + 3 < section1Limit) ? lines.get(i + 3) : "";
                String status = (i + 4 < section1Limit) ? lines.get(i + 4) : "";
                String lastAction = (i + 5 < section1Limit) ? lines.get(i + 5) : "";

                currentEquipments.add(ParsedEquipment.builder()
                        .reference(ref)
                        .name(name)
                        .type(type)
                        .location(loc)
                        .status(status)
                        .lastAction(lastAction)
                        .build());

                i += 6;
                continue;
            }

            // Línea decorativa o cabecera de cliente
            if (!inEquiposBlock) {
                if (!line.equalsIgnoreCase("Contactos") && !line.equalsIgnoreCase("CONTACTO")
                        && !line.equalsIgnoreCase("Cliente") && !line.equalsIgnoreCase("CLIENTE")) {
                    currentCandidateHeader = line;
                }
            }

            i++;
        }

        // Finalizar último cliente de la Sección 1
        if (!currentFields.isEmpty() || !currentCandidateHeader.isEmpty()) {
            ParsedClient finishedClient = buildClient(currentFields, currentCandidateHeader, currentEquipments);
            if (finishedClient != null) {
                parsedClients.add(finishedClient);
            }
        }

        result.setClients(parsedClients);

        // 2. Parsear Sección 2: Otros Dispositivos (si existe)
        if (section2Index != -1) {
            List<ParsedEquipment> otherEquipos = new ArrayList<>();
            int j = section2Index;
            while (j < lines.size()) {
                String l = lines.get(j);
                if (isEquipmentReference(l)) {
                    String ref = l;
                    String eqName = (j + 1 < lines.size()) ? lines.get(j + 1) : "";
                    String eqType = (j + 2 < lines.size()) ? lines.get(j + 2) : "";
                    String clientName = (j + 3 < lines.size()) ? lines.get(j + 3) : "";
                    String lastRev = (j + 4 < lines.size()) ? lines.get(j + 4) : "";

                    otherEquipos.add(ParsedEquipment.builder()
                            .reference(ref)
                            .name(eqName)
                            .type(eqType)
                            .clientName(clientName)
                            .lastAction(lastRev)
                            .build());

                    j += 5;
                    while (j < lines.size() && (!isEquipmentReference(lines.get(j)) && (lines.get(j).isEmpty() || lines.get(j).equalsIgnoreCase("Opciones") || lines.get(j).equalsIgnoreCase("Sin acciones")))) {
                        j++;
                    }
                    continue;
                }
                j++;
            }
            result.setOtherEquipments(otherEquipos);
        }

        return result;
    }

    private boolean isKeyOrHeader(String line) {
        if (line == null || line.isEmpty()) return false;
        if (line.contains("\t")) return true;
        String lower = line.toLowerCase().replace(":", "");
        if (CLIENT_FIELD_NAMES.contains(lower)) return true;
        return line.equalsIgnoreCase("EQUIPOS") || line.equalsIgnoreCase("EQUIPOS:");
    }

    private boolean isEquipmentReference(String line) {
        if (line == null) return false;
        return EQUIPMENT_REF_PATTERN.matcher(line.trim()).matches();
    }

    private ParsedClient buildClient(Map<String, String> fields, String candidateHeader, List<ParsedEquipment> equipments) {
        String codigo = getField(fields, "código", "codigo");
        String nifCif = getField(fields, "cif/nif", "cif", "nif");
        String nombre = getField(fields, "nombre completo", "nombre");

        if (nombre == null || nombre.isBlank()) {
            nombre = candidateHeader;
        }

        if ((codigo == null || codigo.isBlank()) && (nifCif == null || nifCif.isBlank()) && (nombre == null || nombre.isBlank())) {
            return null;
        }

        Integer eqDeclarados = null;
        String eqDeclStr = getField(fields, "equipos/dispositivos inventariados");
        if (eqDeclStr != null && !eqDeclStr.isBlank()) {
            try {
                eqDeclarados = Integer.parseInt(eqDeclStr.trim());
            } catch (Exception ignored) {}
        }

        Integer websReg = null;
        String websRegStr = getField(fields, "webs registradas");
        if (websRegStr != null && !websRegStr.isBlank()) {
            try {
                websReg = Integer.parseInt(websRegStr.trim());
            } catch (Exception ignored) {}
        }

        // Regla 12: Si equipos/dispositivos declarados = 0 y no hay equipos, lista vacía
        List<ParsedEquipment> clientEquipments = new ArrayList<>(equipments);
        if (eqDeclarados != null && eqDeclarados == 0 && clientEquipments.isEmpty()) {
            clientEquipments = Collections.emptyList();
        }

        // Asociar clientCodigo a los equipos
        for (ParsedEquipment eq : clientEquipments) {
            eq.setClientCodigo(codigo);
            if (eq.getClientName() == null || eq.getClientName().isBlank()) {
                eq.setClientName(nombre);
            }
        }

        // Extraer contactos asociados al cliente
        List<ParsedContact> clientContacts = new ArrayList<>();

        String gerenteName = getField(fields, "gerente", "nombre del gerente", "nombre gerente");
        String emailGerente = getField(fields, "email de gerente", "email gerente", "correo de gerente", "correo gerente");

        String personaTicName = getField(fields, "persona tic", "responsable tic", "tic");
        String emailPersonaTic = getField(fields, "email persona tic", "email tic", "correo persona tic", "correo tic");

        String telefono = getField(fields, "teléfono de contacto", "telefono de contacto", "teléfono", "telefono", "teléfono fijo", "telefono fijo", "móvil", "movil");

        String genericContactName = getField(fields, "nombre de contacto", "persona de contacto", "contacto");
        String genericContactEmail = getField(fields, "email", "correo", "email de contacto", "correo de contacto");
        String genericContactRole = getField(fields, "cargo", "rol", "puesto");

        // 1. Gerente (Reglas 1, 5, 7, 8)
        ParsedContact gerenteContact = null;
        boolean hasGerenteInfo = (gerenteName != null && !gerenteName.isBlank()) || (emailGerente != null && !emailGerente.isBlank());
        if (hasGerenteInfo) {
            String contactName = (gerenteName != null && !gerenteName.isBlank()) ? gerenteName.trim() : "Gerente";
            gerenteContact = ParsedContact.builder()
                    .nombre(contactName)
                    .cargo("Gerente")
                    .email(emailGerente != null && !emailGerente.isBlank() ? emailGerente.trim() : null)
                    .clientCodigo(codigo)
                    .clientNombre(nombre)
                    .build();
            clientContacts.add(gerenteContact);
        }

        // 2. Persona TIC (Reglas 1, 5, 7, 8)
        ParsedContact ticContact = null;
        boolean hasTicInfo = (personaTicName != null && !personaTicName.isBlank()) || (emailPersonaTic != null && !emailPersonaTic.isBlank());
        if (hasTicInfo) {
            String contactName = (personaTicName != null && !personaTicName.isBlank()) ? personaTicName.trim() : "Persona TIC";
            ticContact = ParsedContact.builder()
                    .nombre(contactName)
                    .cargo("Persona TIC")
                    .email(emailPersonaTic != null && !emailPersonaTic.isBlank() ? emailPersonaTic.trim() : null)
                    .clientCodigo(codigo)
                    .clientNombre(nombre)
                    .build();
            clientContacts.add(ticContact);
        }

        // 3. Contacto Genérico adicional si no duplica Gerente ni TIC
        if ((genericContactName != null && !genericContactName.isBlank()) || (genericContactEmail != null && !genericContactEmail.isBlank())) {
            String cName = (genericContactName != null && !genericContactName.isBlank()) ? genericContactName.trim() : "Contacto";
            boolean isRedundant = (gerenteContact != null && (gerenteContact.getNombre().equalsIgnoreCase(cName) || (genericContactEmail != null && genericContactEmail.equalsIgnoreCase(gerenteContact.getEmail()))))
                    || (ticContact != null && (ticContact.getNombre().equalsIgnoreCase(cName) || (genericContactEmail != null && genericContactEmail.equalsIgnoreCase(ticContact.getEmail()))));
            if (!isRedundant) {
                clientContacts.add(ParsedContact.builder()
                        .nombre(cName)
                        .cargo(genericContactRole != null && !genericContactRole.isBlank() ? genericContactRole.trim() : "Contacto")
                        .email(genericContactEmail != null && !genericContactEmail.isBlank() ? genericContactEmail.trim() : null)
                        .clientCodigo(codigo)
                        .clientNombre(nombre)
                        .build());
            }
        }

        // 4. Asociación de Teléfono (Regla 6):
        if (telefono != null && !telefono.isBlank()) {
            String telTrimmed = telefono.trim();
            if (gerenteContact != null) {
                // Caso A: Asociar al contacto de Gerencia
                gerenteContact.setTelefono(telTrimmed);
            } else if (!clientContacts.isEmpty()) {
                // Asociar al primer contacto identificado
                clientContacts.get(0).setTelefono(telTrimmed);
            } else {
                // Caso C: No puede saberse a qué persona pertenece -> Guardar como contacto general del cliente
                clientContacts.add(ParsedContact.builder()
                        .nombre("Contacto General")
                        .cargo("Contacto General")
                        .telefono(telTrimmed)
                        .clientCodigo(codigo)
                        .clientNombre(nombre)
                        .build());
            }
        }

        return ParsedClient.builder()
                .codigo(codigo != null ? codigo.trim() : null)
                .nifCif(nifCif != null ? nifCif.trim().toUpperCase() : null)
                .nombre(nombre != null ? nombre.trim() : null)
                .razonSocial(getField(fields, "razón social", "razon social"))
                .estado(getField(fields, "estado"))
                .mantenimiento(getField(fields, "mantenimiento"))
                .diaMantenimiento(getField(fields, "dia mantenimiento"))
                .tecnico(getField(fields, "técnico que realiza la visita", "tecnico que realiza la visita"))
                .visitasMes(getField(fields, "número de visitas en el mes", "numero de visitas en el mes"))
                .equiposDeclarados(eqDeclarados)
                .websRegistradas(websReg)
                .direccion(getField(fields, "dirección", "direccion"))
                .poblacion(getField(fields, "población", "poblacion"))
                .provincia(getField(fields, "provincia"))
                .gerente(gerenteName != null ? gerenteName.trim() : null)
                .emailGerente(emailGerente != null ? emailGerente.trim() : null)
                .personaTic(personaTicName != null ? personaTicName.trim() : null)
                .emailPersonaTic(emailPersonaTic != null ? emailPersonaTic.trim() : null)
                .telefono(telefono != null ? telefono.trim() : null)
                .fechaAltaStr(getField(fields, "fecha alta"))
                .equipments(clientEquipments)
                .contacts(clientContacts)
                .build();
    }

    private String getField(Map<String, String> fields, String... possibleKeys) {
        for (String k : possibleKeys) {
            if (fields.containsKey(k) && fields.get(k) != null) {
                return fields.get(k);
            }
        }
        return null;
    }
}
