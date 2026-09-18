package com.abaxial.portal.clients.importer.parser;

import com.abaxial.portal.clients.importer.dto.ParsedClient;
import com.abaxial.portal.clients.importer.dto.ParsedContact;
import com.abaxial.portal.clients.importer.dto.ParsedEquipment;
import com.abaxial.portal.clients.importer.dto.ParsedImportData;
import com.abaxial.portal.clients.importer.normalizer.DataNormalizer;
import lombok.RequiredArgsConstructor;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
@RequiredArgsConstructor
public class HtmlDocumentParser implements DocumentParser {

    private final TextDocumentParser textDocumentParser;

    private static final Pattern CODE_IN_URL = Pattern.compile("codigo=([A-Za-z0-9]+)", Pattern.CASE_INSENSITIVE);
    private static final Pattern REF_IN_URL = Pattern.compile("ref=([A-Za-z0-9_-]+)", Pattern.CASE_INSENSITIVE);

    @Override
    public boolean supports(String filename, String contentType) {
        if (filename == null) return false;
        String lower = filename.toLowerCase();
        return lower.endsWith(".html") || lower.endsWith(".htm") ||
               (contentType != null && (contentType.contains("text/html") || contentType.contains("application/xhtml+xml")));
    }

    @Override
    public ParsedImportData parse(InputStream inputStream, String filename) {
        try {
            Document doc = Jsoup.parse(inputStream, StandardCharsets.UTF_8.name(), "");

            // Sanitizar: Eliminar scripts, iframes, styles para máxima seguridad
            doc.select("script, style, iframe, object, embed").remove();

            ParsedImportData importData = new ParsedImportData();

            // 1. Analizar si el HTML contiene tablas estructuradas
            Elements tables = doc.select("table");
            if (!tables.isEmpty()) {
                parseHtmlTables(doc, tables, importData);
            }

            // Si no se extrajeron clientes de tablas, o como fallback estructurado,
            // extraer texto estructural respetando saltos de línea en headings, párrafos y celdas
            if (importData.getClients().isEmpty()) {
                List<String> structuredLines = extractStructuredLines(doc);
                ParsedImportData fromText = textDocumentParser.parseLines(structuredLines);
                importData.setClients(fromText.getClients());
                if (importData.getOtherEquipments().isEmpty()) {
                    importData.setOtherEquipments(fromText.getOtherEquipments());
                }
            }

            // Enriquecer con códigos y referencias detectados en enlaces (ej. <a href="...codigo=43000007">)
            enrichFromLinks(doc, importData);

            return importData;
        } catch (Exception e) {
            throw new RuntimeException("Error al parsear documento HTML: " + e.getMessage(), e);
        }
    }

    private void parseHtmlTables(Document doc, Elements tables, ParsedImportData importData) {
        for (Element table : tables) {
            Elements rows = table.select("tr");
            if (rows.isEmpty()) continue;

            // Verificar si es una tabla de clave-valor (ej. 2 columnas con campos de cliente)
            boolean isKeyValueTable = true;
            for (Element row : rows) {
                Elements cells = row.select("td, th");
                if (cells.size() != 2 && !cells.isEmpty()) {
                    isKeyValueTable = false;
                    break;
                }
            }

            // Si es tabla de equipos (ej. encabezados con Ref, Equipo, Tipo, etc.)
            Element firstRow = rows.first();
            Elements headers = firstRow.select("th, td");
            boolean isEquipmentTable = false;
            boolean isContactTable = false;
            for (Element h : headers) {
                String text = DataNormalizer.normalizeName(h.text());
                if (text.contains("ref") || text.contains("inventario") || text.contains("equipo") || text.contains("dispositivo")) {
                    isEquipmentTable = true;
                }
                if (text.contains("contacto") || text.contains("gerente") || text.contains("cargo") || text.contains("puesto")) {
                    isContactTable = true;
                }
            }

            if (isEquipmentTable && !isKeyValueTable) {
                ParsedClient lastClient = importData.getClients().isEmpty() ? null : importData.getClients().get(importData.getClients().size() - 1);
                List<ParsedEquipment> tableEquipments = parseEquipmentTableRows(rows, lastClient);
                if (!tableEquipments.isEmpty()) {
                    if (lastClient != null) {
                        lastClient.getEquipments().addAll(tableEquipments);
                    } else {
                        importData.getOtherEquipments().addAll(tableEquipments);
                    }
                }
            } else if (isContactTable && !isKeyValueTable) {
                ParsedClient lastClient = importData.getClients().isEmpty() ? null : importData.getClients().get(importData.getClients().size() - 1);
                List<ParsedContact> tableContacts = parseContactTableRows(rows, lastClient);
                if (!tableContacts.isEmpty() && lastClient != null) {
                    lastClient.getContacts().addAll(tableContacts);
                }
            }
        }
    }

    private List<ParsedContact> parseContactTableRows(Elements rows, ParsedClient lastClient) {
        List<ParsedContact> list = new ArrayList<>();
        if (rows.size() <= 1) return list;

        Element headerRow = rows.first();
        Elements headers = headerRow.select("th, td");
        int nameCol = -1;
        int roleCol = -1;
        int emailCol = -1;
        int phoneCol = -1;

        for (int i = 0; i < headers.size(); i++) {
            String colName = DataNormalizer.normalizeName(headers.get(i).text());
            if (colName.contains("nombre") || colName.contains("contacto") || colName.contains("persona")) nameCol = i;
            else if (colName.contains("cargo") || colName.contains("rol") || colName.contains("puesto")) roleCol = i;
            else if (colName.contains("email") || colName.contains("correo")) emailCol = i;
            else if (colName.contains("telefono") || colName.contains("movil") || colName.contains("fijo")) phoneCol = i;
        }

        for (int r = 1; r < rows.size(); r++) {
            Elements cells = rows.get(r).select("td, th");
            if (cells.isEmpty()) continue;

            String name = (nameCol != -1 && nameCol < cells.size()) ? cells.get(nameCol).text().trim() : "";
            String email = (emailCol != -1 && emailCol < cells.size()) ? cells.get(emailCol).text().trim() : "";
            String role = (roleCol != -1 && roleCol < cells.size()) ? cells.get(roleCol).text().trim() : "Contacto";
            String phone = (phoneCol != -1 && phoneCol < cells.size()) ? cells.get(phoneCol).text().trim() : "";

            if (name.isEmpty() && email.isEmpty() && phone.isEmpty()) continue;
            if (name.isEmpty()) {
                name = role.equalsIgnoreCase("Gerente") ? "Gerente" : "Contacto";
            }

            list.add(ParsedContact.builder()
                    .nombre(name)
                    .cargo(role)
                    .email(!email.isEmpty() ? email : null)
                    .telefono(!phone.isEmpty() ? phone : null)
                    .clientCodigo(lastClient != null ? lastClient.getCodigo() : null)
                    .clientNombre(lastClient != null ? lastClient.getNombre() : null)
                    .build());
        }
        return list;
    }

    private List<ParsedEquipment> parseEquipmentTableRows(Elements rows, ParsedClient lastClient) {
        List<ParsedEquipment> list = new ArrayList<>();
        if (rows.size() <= 1) return list;

        // Mapear posiciones de columnas según encabezados
        Element headerRow = rows.first();
        Elements headers = headerRow.select("th, td");
        int refCol = -1;
        int nameCol = -1;
        int typeCol = -1;
        int clientCol = -1;
        int statusCol = -1;
        int locationCol = -1;
        int actionCol = -1;

        for (int i = 0; i < headers.size(); i++) {
            String colName = DataNormalizer.normalizeName(headers.get(i).text());
            if (colName.contains("ref") || colName.contains("codigo")) refCol = i;
            else if (colName.contains("equipo") || colName.contains("nombre") || colName.contains("dispositivo")) nameCol = i;
            else if (colName.contains("tipo")) typeCol = i;
            else if (colName.contains("cliente")) clientCol = i;
            else if (colName.contains("estado")) statusCol = i;
            else if (colName.contains("ubicacion") || colName.contains("lugar")) locationCol = i;
            else if (colName.contains("revision") || colName.contains("accion") || colName.contains("fecha")) actionCol = i;
        }

        for (int r = 1; r < rows.size(); r++) {
            Elements cells = rows.get(r).select("td, th");
            if (cells.isEmpty()) continue;

            String ref = (refCol != -1 && refCol < cells.size()) ? cells.get(refCol).text().trim() : "";
            if (ref.isEmpty()) continue;

            String name = (nameCol != -1 && nameCol < cells.size()) ? cells.get(nameCol).text().trim() : "";
            String type = (typeCol != -1 && typeCol < cells.size()) ? cells.get(typeCol).text().trim() : "";
            String client = (clientCol != -1 && clientCol < cells.size()) ? cells.get(clientCol).text().trim() : (lastClient != null ? (lastClient.getCodigo() != null ? lastClient.getCodigo() : lastClient.getNombre()) : "");
            String status = (statusCol != -1 && statusCol < cells.size()) ? cells.get(statusCol).text().trim() : "";
            String location = (locationCol != -1 && locationCol < cells.size()) ? cells.get(locationCol).text().trim() : "";
            String action = (actionCol != -1 && actionCol < cells.size()) ? cells.get(actionCol).text().trim() : "";

            // URL si hay enlace en la fila
            String url = "";
            Element link = cells.select("a[href]").first();
            if (link != null) {
                url = link.attr("href");
            }

            list.add(ParsedEquipment.builder()
                    .reference(ref)
                    .name(name)
                    .type(type)
                    .clientName(client)
                    .status(status)
                    .location(location)
                    .lastAction(action)
                    .url(url)
                    .build());
        }
        return list;
    }

    private List<String> extractStructuredLines(Document doc) {
        List<String> lines = new ArrayList<>();
        // Recorrer bloques relevantes
        Elements blocks = doc.select("h1, h2, h3, h4, h5, h6, p, tr, li, div");
        for (Element block : blocks) {
            // Si el bloque tiene celdas td/th, formatear como key\tvalue
            Elements cells = block.select("td, th");
            if (cells.size() == 2) {
                lines.add(cells.get(0).text().trim() + "\t" + cells.get(1).text().trim());
            } else {
                String ownText = block.ownText().trim();
                if (!ownText.isEmpty()) {
                    lines.add(ownText);
                }
            }
        }
        return lines;
    }

    private void enrichFromLinks(Document doc, ParsedImportData importData) {
        Elements links = doc.select("a[href]");
        for (Element link : links) {
            String href = link.attr("href");
            Matcher codeMatcher = CODE_IN_URL.matcher(href);
            if (codeMatcher.find()) {
                String codeFound = codeMatcher.group(1);
                String linkText = link.text().trim();
                // Buscar si coincide con algún cliente sin código
                for (ParsedClient c : importData.getClients()) {
                    if ((c.getCodigo() == null || c.getCodigo().isBlank()) && c.getNombre() != null && c.getNombre().equalsIgnoreCase(linkText)) {
                        c.setCodigo(codeFound);
                    }
                }
            }

            Matcher refMatcher = REF_IN_URL.matcher(href);
            if (refMatcher.find()) {
                String refFound = refMatcher.group(1);
                for (ParsedEquipment eq : importData.getOtherEquipments()) {
                    if (eq.getReference() != null && eq.getReference().equalsIgnoreCase(refFound)) {
                        if (eq.getUrl() == null || eq.getUrl().isBlank()) {
                            eq.setUrl(href);
                        }
                    }
                }
            }
        }
    }
}
