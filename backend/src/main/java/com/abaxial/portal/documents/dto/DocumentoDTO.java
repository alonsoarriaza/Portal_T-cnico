package com.abaxial.portal.documents.dto;

import com.abaxial.portal.documents.entity.Documento;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DocumentoDTO {
    private Long id;
    private Long clienteId;
    private String clienteNombre;
    private String nombreOriginal;
    private String categoria;
    private String descripcion;
    private Integer versionActual;
    private boolean activo;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaModificacion;
    private DocumentoVersionDTO ultimaVersion;
    private List<DocumentoVersionDTO> versiones;

    public static DocumentoDTO fromEntity(Documento d) {
        List<DocumentoVersionDTO> versList = d.getVersiones() != null
                ? d.getVersiones().stream().map(DocumentoVersionDTO::fromEntity).collect(Collectors.toList())
                : List.of();

        DocumentoVersionDTO ultima = versList.isEmpty() ? null : versList.get(0);

        return DocumentoDTO.builder()
                .id(d.getId())
                .clienteId(d.getCliente() != null ? d.getCliente().getId() : null)
                .clienteNombre(d.getCliente() != null ? d.getCliente().getNombre() : null)
                .nombreOriginal(d.getNombreOriginal())
                .categoria(d.getCategoria())
                .descripcion(d.getDescripcion())
                .versionActual(d.getVersionActual())
                .activo(Boolean.TRUE.equals(d.getActivo()))
                .fechaCreacion(d.getFechaCreacion())
                .fechaModificacion(d.getFechaModificacion())
                .ultimaVersion(ultima)
                .versiones(versList)
                .build();
    }
}
