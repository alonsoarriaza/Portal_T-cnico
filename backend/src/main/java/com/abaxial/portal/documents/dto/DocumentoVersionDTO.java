package com.abaxial.portal.documents.dto;

import com.abaxial.portal.documents.entity.DocumentoVersion;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DocumentoVersionDTO {
    private Long id;
    private Long documentoId;
    private Integer version;
    private String nombreArchivo;
    private String extension;
    private String mimeType;
    private Long tamano;
    private Long usuarioId;
    private String usuarioNombre;
    private LocalDateTime fechaSubida;

    public static DocumentoVersionDTO fromEntity(DocumentoVersion v) {
        return DocumentoVersionDTO.builder()
                .id(v.getId())
                .documentoId(v.getDocumento() != null ? v.getDocumento().getId() : null)
                .version(v.getVersion())
                .nombreArchivo(v.getNombreArchivo())
                .extension(v.getExtension())
                .mimeType(v.getMimeType())
                .tamano(v.getTamano())
                .usuarioId(v.getUsuario() != null ? v.getUsuario().getId() : null)
                .usuarioNombre(v.getUsuario() != null ? v.getUsuario().getNombreCompleto() : "SISTEMA")
                .fechaSubida(v.getFechaSubida())
                .build();
    }
}
