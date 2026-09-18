package com.abaxial.portal.clients.dto;

import com.abaxial.portal.clients.entity.Cliente;
import com.abaxial.portal.contacts.dto.ContactoDTO;
import com.abaxial.portal.documents.dto.DocumentoDTO;
import com.abaxial.portal.equipment.dto.EquipoDTO;
import com.abaxial.portal.events.dto.EventoDTO;
import com.abaxial.portal.services.dto.ServicioDTO;
import com.abaxial.portal.websites.dto.WebDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClienteDetailDTO {
    private Long id;
    private String codigo;
    private String nifCif;
    private String nombre;
    private String estado;
    private String mantenimiento;
    private String direccion;
    private String poblacion;
    private String provincia;
    private String gerente;
    private LocalDate fechaAlta;
    private boolean activo;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaModificacion;

    private List<ContactoDTO> contactos;
    private List<EquipoDTO> equipos;
    private List<ServicioDTO> servicios;
    private List<WebDTO> webs;
    private List<DocumentoDTO> documentos;
    private List<EventoDTO> eventos;

    public static ClienteDetailDTO fromEntity(Cliente c) {
        List<ContactoDTO> contactsList = c.getContactos() != null
                ? c.getContactos().stream().filter(ct -> Boolean.TRUE.equals(ct.getActivo())).map(ContactoDTO::fromEntity).collect(Collectors.toList())
                : List.of();

        List<EquipoDTO> eqList = c.getEquipos() != null
                ? c.getEquipos().stream().filter(e -> Boolean.TRUE.equals(e.getActivo())).map(EquipoDTO::fromEntity).collect(Collectors.toList())
                : List.of();

        List<ServicioDTO> srvList = c.getServicios() != null
                ? c.getServicios().stream().filter(s -> Boolean.TRUE.equals(s.getActivo())).map(ServicioDTO::fromEntity).collect(Collectors.toList())
                : List.of();

        List<WebDTO> webList = c.getWebs() != null
                ? c.getWebs().stream().filter(w -> Boolean.TRUE.equals(w.getActivo())).map(WebDTO::fromEntity).collect(Collectors.toList())
                : List.of();

        List<DocumentoDTO> docList = c.getDocumentos() != null
                ? c.getDocumentos().stream()
                    .filter(d -> Boolean.TRUE.equals(d.getActivo()))
                    .sorted((d1, d2) -> String.CASE_INSENSITIVE_ORDER.compare(
                            d1.getNombreOriginal() != null ? d1.getNombreOriginal() : "",
                            d2.getNombreOriginal() != null ? d2.getNombreOriginal() : ""))
                    .map(DocumentoDTO::fromEntity)
                    .collect(Collectors.toList())
                : List.of();

        List<EventoDTO> evList = c.getEventos() != null
                ? c.getEventos().stream().filter(e -> Boolean.TRUE.equals(e.getActivo())).map(EventoDTO::fromEntity).collect(Collectors.toList())
                : List.of();

        return ClienteDetailDTO.builder()
                .id(c.getId())
                .codigo(c.getCodigo())
                .nifCif(c.getNifCif())
                .nombre(c.getNombre())
                .estado(c.getEstado())
                .mantenimiento(c.getMantenimiento())
                .direccion(c.getDireccion())
                .poblacion(c.getPoblacion())
                .provincia(c.getProvincia())
                .gerente(c.getGerente())
                .fechaAlta(c.getFechaAlta())
                .activo(Boolean.TRUE.equals(c.getActivo()))
                .fechaCreacion(c.getFechaCreacion())
                .fechaModificacion(c.getFechaModificacion())
                .contactos(contactsList)
                .equipos(eqList)
                .servicios(srvList)
                .webs(webList)
                .documentos(docList)
                .eventos(evList)
                .build();
    }
}
