package com.abaxial.portal.pdf.service;

import com.abaxial.portal.clients.dto.ClienteDetailDTO;
import com.abaxial.portal.clients.service.ClienteService;
import com.abaxial.portal.contacts.dto.ContactoDTO;
import com.abaxial.portal.documents.dto.DocumentoDTO;
import com.abaxial.portal.equipment.dto.EquipoDTO;
import com.abaxial.portal.services.dto.ServicioDTO;
import com.abaxial.portal.websites.dto.WebDTO;
import com.lowagie.text.*;
import com.lowagie.text.pdf.*;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
public class PdfExportService {

    private static final Logger log = LoggerFactory.getLogger(PdfExportService.class);
    private final ClienteService clienteService;

    private static final Color PRIMARY_COLOR = new Color(15, 23, 42);     // Slate 900
    private static final Color ACCENT_COLOR = new Color(14, 116, 144);    // Cyan 700
    private static final Color HEADER_BG = new Color(241, 245, 249);      // Slate 100
    private static final Color BORDER_COLOR = new Color(203, 213, 225);   // Slate 300

    public byte[] generarFichaClientePdf(Long clienteId) {
        ClienteDetailDTO cliente = clienteService.obtenerDetalleCliente(clienteId);

        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Document document = new Document(PageSize.A4, 36, 36, 40, 40);
            PdfWriter.getInstance(document, out);
            document.open();

            // Tipografías
            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, PRIMARY_COLOR);
            Font subtitleFont = FontFactory.getFont(FontFactory.HELVETICA, 10, Color.DARK_GRAY);
            Font sectionTitleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, ACCENT_COLOR);
            Font boldFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9, PRIMARY_COLOR);
            Font normalFont = FontFactory.getFont(FontFactory.HELVETICA, 9, Color.BLACK);
            Font headerTableFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9, Color.WHITE);

            // Cabecera Corporativa
            PdfPTable headerTable = new PdfPTable(2);
            headerTable.setWidthPercentage(100);
            headerTable.setWidths(new float[]{60f, 40f});
            headerTable.getDefaultCell().setBorder(Rectangle.NO_BORDER);

            PdfPCell leftCell = new PdfPCell();
            leftCell.setBorder(Rectangle.NO_BORDER);
            leftCell.addElement(new Paragraph("ABAXIAL PORTAL TÉCNICO", titleFont));
            leftCell.addElement(new Paragraph("Ficha Técnica Integral de Cliente", subtitleFont));

            PdfPCell rightCell = new PdfPCell();
            rightCell.setBorder(Rectangle.NO_BORDER);
            rightCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            String fechaGeneracion = java.time.LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
            Paragraph genPar = new Paragraph("Fecha: " + fechaGeneracion + "\nCódigo: " + cliente.getCodigo(), subtitleFont);
            genPar.setAlignment(Element.ALIGN_RIGHT);
            rightCell.addElement(genPar);

            headerTable.addCell(leftCell);
            headerTable.addCell(rightCell);
            document.add(headerTable);

            document.add(new Paragraph(" ")); // Spacer

            // 1. Datos Generales del Cliente
            document.add(new Paragraph("1. INFORMACIÓN GENERAL", sectionTitleFont));
            document.add(new Paragraph(" "));

            PdfPTable infoTable = new PdfPTable(4);
            infoTable.setWidthPercentage(100);
            infoTable.setWidths(new float[]{20f, 30f, 20f, 30f});

            addInfoCell(infoTable, "Nombre / Razón Social:", boldFont);
            addInfoCell(infoTable, cliente.getNombre(), normalFont);
            addInfoCell(infoTable, "NIF / CIF:", boldFont);
            addInfoCell(infoTable, cliente.getNifCif(), normalFont);

            addInfoCell(infoTable, "Código:", boldFont);
            addInfoCell(infoTable, cliente.getCodigo(), normalFont);
            addInfoCell(infoTable, "Estado:", boldFont);
            addInfoCell(infoTable, cliente.getEstado(), normalFont);

            addInfoCell(infoTable, "Mantenimiento:", boldFont);
            addInfoCell(infoTable, cliente.getMantenimiento() != null ? cliente.getMantenimiento() : "N/A", normalFont);
            addInfoCell(infoTable, "Fecha Alta:", boldFont);
            addInfoCell(infoTable, cliente.getFechaAlta() != null ? cliente.getFechaAlta().toString() : "N/A", normalFont);

            addInfoCell(infoTable, "Dirección:", boldFont);
            addInfoCell(infoTable, cliente.getDireccion() != null ? cliente.getDireccion() : "N/A", normalFont);
            addInfoCell(infoTable, "Población / Prov.:", boldFont);
            addInfoCell(infoTable, (cliente.getPoblacion() != null ? cliente.getPoblacion() : "") + " (" + (cliente.getProvincia() != null ? cliente.getProvincia() : "") + ")", normalFont);

            addInfoCell(infoTable, "Gerente / Resp.:", boldFont);
            addInfoCell(infoTable, cliente.getGerente() != null ? cliente.getGerente() : "N/A", normalFont);
            addInfoCell(infoTable, "Activo:", boldFont);
            addInfoCell(infoTable, cliente.isActivo() ? "SÍ" : "NO (Baja)", normalFont);

            document.add(infoTable);
            document.add(new Paragraph(" "));

            // 2. Contactos
            document.add(new Paragraph("2. PERSONAS DE CONTACTO (" + (cliente.getContactos() != null ? cliente.getContactos().size() : 0) + ")", sectionTitleFont));
            document.add(new Paragraph(" "));

            PdfPTable contTable = new PdfPTable(4);
            contTable.setWidthPercentage(100);
            contTable.setWidths(new float[]{30f, 25f, 25f, 20f});
            addHeaderCell(contTable, "Nombre Completo", headerTableFont);
            addHeaderCell(contTable, "Cargo", headerTableFont);
            addHeaderCell(contTable, "Email", headerTableFont);
            addHeaderCell(contTable, "Teléfonos", headerTableFont);

            if (cliente.getContactos() != null && !cliente.getContactos().isEmpty()) {
                for (ContactoDTO c : cliente.getContactos()) {
                    addTableCell(contTable, (c.getNombre() != null ? c.getNombre() : "") + " " + (c.getApellidos() != null ? c.getApellidos() : ""), normalFont);
                    addTableCell(contTable, c.getCargo() != null ? c.getCargo() : "-", normalFont);
                    addTableCell(contTable, c.getEmail() != null ? c.getEmail() : "-", normalFont);
                    addTableCell(contTable, (c.getTelefono() != null ? c.getTelefono() : "") + " " + (c.getTelefonoFijo() != null ? "/ " + c.getTelefonoFijo() : ""), normalFont);
                }
            } else {
                PdfPCell emptyCell = new PdfPCell(new Phrase("No hay contactos registrados.", normalFont));
                emptyCell.setColspan(4);
                emptyCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                emptyCell.setPadding(6);
                contTable.addCell(emptyCell);
            }
            document.add(contTable);
            document.add(new Paragraph(" "));

            // 3. Equipos
            document.add(new Paragraph("3. PARQUE INFORMÁTICO Y EQUIPOS (" + (cliente.getEquipos() != null ? cliente.getEquipos().size() : 0) + ")", sectionTitleFont));
            document.add(new Paragraph(" "));

            PdfPTable eqTable = new PdfPTable(5);
            eqTable.setWidthPercentage(100);
            eqTable.setWidths(new float[]{20f, 20f, 25f, 20f, 15f});
            addHeaderCell(eqTable, "Cód. Inventario", headerTableFont);
            addHeaderCell(eqTable, "Tipo", headerTableFont);
            addHeaderCell(eqTable, "Marca / Modelo", headerTableFont);
            addHeaderCell(eqTable, "Nº Serie", headerTableFont);
            addHeaderCell(eqTable, "Estado", headerTableFont);

            if (cliente.getEquipos() != null && !cliente.getEquipos().isEmpty()) {
                for (EquipoDTO e : cliente.getEquipos()) {
                    addTableCell(eqTable, e.getCodigoInventario(), boldFont);
                    addTableCell(eqTable, e.getTipo(), normalFont);
                    addTableCell(eqTable, (e.getMarca() != null ? e.getMarca() : "") + " " + (e.getModelo() != null ? e.getModelo() : ""), normalFont);
                    addTableCell(eqTable, e.getNumeroSerie() != null ? e.getNumeroSerie() : "-", normalFont);
                    addTableCell(eqTable, e.getEstado(), normalFont);
                }
            } else {
                PdfPCell emptyCell = new PdfPCell(new Phrase("No hay equipos registrados.", normalFont));
                emptyCell.setColspan(5);
                emptyCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                emptyCell.setPadding(6);
                eqTable.addCell(emptyCell);
            }
            document.add(eqTable);
            document.add(new Paragraph(" "));

            // 4. Servicios Contratados
            document.add(new Paragraph("4. SERVICIOS Y CONTRATOS (" + (cliente.getServicios() != null ? cliente.getServicios().size() : 0) + ")", sectionTitleFont));
            document.add(new Paragraph(" "));

            PdfPTable srvTable = new PdfPTable(4);
            srvTable.setWidthPercentage(100);
            srvTable.setWidths(new float[]{35f, 20f, 25f, 20f});
            addHeaderCell(srvTable, "Servicio", headerTableFont);
            addHeaderCell(srvTable, "Estado", headerTableFont);
            addHeaderCell(srvTable, "Vigencia (Inicio - Fin)", headerTableFont);
            addHeaderCell(srvTable, "Observaciones", headerTableFont);

            if (cliente.getServicios() != null && !cliente.getServicios().isEmpty()) {
                for (ServicioDTO s : cliente.getServicios()) {
                    addTableCell(srvTable, s.getNombre(), boldFont);
                    addTableCell(srvTable, s.getEstado(), normalFont);
                    String vigencia = (s.getFechaInicio() != null ? s.getFechaInicio().toString() : "-") + " / " + (s.getFechaFin() != null ? s.getFechaFin().toString() : "Indefinido");
                    addTableCell(srvTable, vigencia, normalFont);
                    addTableCell(srvTable, s.getObservaciones() != null ? s.getObservaciones() : "-", normalFont);
                }
            } else {
                PdfPCell emptyCell = new PdfPCell(new Phrase("No hay servicios contratados.", normalFont));
                emptyCell.setColspan(4);
                emptyCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                emptyCell.setPadding(6);
                srvTable.addCell(emptyCell);
            }
            document.add(srvTable);
            document.add(new Paragraph(" "));

            // 5. Webs y Dominios
            document.add(new Paragraph("5. SITIOS WEB Y DOMINIOS (" + (cliente.getWebs() != null ? cliente.getWebs().size() : 0) + ")", sectionTitleFont));
            document.add(new Paragraph(" "));

            PdfPTable webTable = new PdfPTable(3);
            webTable.setWidthPercentage(100);
            webTable.setWidths(new float[]{35f, 45f, 20f});
            addHeaderCell(webTable, "Nombre Web", headerTableFont);
            addHeaderCell(webTable, "URL / Enlace", headerTableFont);
            addHeaderCell(webTable, "Estado", headerTableFont);

            if (cliente.getWebs() != null && !cliente.getWebs().isEmpty()) {
                for (WebDTO w : cliente.getWebs()) {
                    addTableCell(webTable, w.getNombre(), boldFont);
                    addTableCell(webTable, w.getUrl(), normalFont);
                    addTableCell(webTable, w.getEstado(), normalFont);
                }
            } else {
                PdfPCell emptyCell = new PdfPCell(new Phrase("No hay webs registradas.", normalFont));
                emptyCell.setColspan(3);
                emptyCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                emptyCell.setPadding(6);
                webTable.addCell(emptyCell);
            }
            document.add(webTable);

            document.close();
            return out.toByteArray();
        } catch (Exception e) {
            log.error("Error generando PDF de ficha de cliente {}: {}", clienteId, e.getMessage());
            throw new RuntimeException("Error al generar PDF de la ficha de cliente", e);
        }
    }

    private void addHeaderCell(PdfPTable table, String text, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setBackgroundColor(PRIMARY_COLOR);
        cell.setPadding(6);
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        table.addCell(cell);
    }

    private void addTableCell(PdfPTable table, String text, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setPadding(5);
        cell.setBorderColor(BORDER_COLOR);
        table.addCell(cell);
    }

    private void addInfoCell(PdfPTable table, String text, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setPadding(5);
        cell.setBackgroundColor(HEADER_BG);
        cell.setBorderColor(BORDER_COLOR);
        table.addCell(cell);
    }
}
