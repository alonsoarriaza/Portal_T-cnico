package com.abaxial.portal.audit.dto;

import com.abaxial.portal.audit.entity.Auditoria;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuditoriaDTO {
    private Long id;
    private Long usuarioId;
    private String username;
    private String accion;
    private String entidad;
    private Long entidadId;
    private LocalDateTime fecha;
    private String ip;
    private String userAgent;
    private String detalles;

    public static AuditoriaDTO fromEntity(Auditoria a) {
        return AuditoriaDTO.builder()
                .id(a.getId())
                .usuarioId(a.getUsuario() != null ? a.getUsuario().getId() : null)
                .username(a.getUsername() != null ? a.getUsername() : (a.getUsuario() != null ? a.getUsuario().getUsername() : "ANÓNIMO"))
                .accion(a.getAccion())
                .entidad(a.getEntidad())
                .entidadId(a.getEntidadId())
                .fecha(a.getFecha())
                .ip(a.getIp())
                .userAgent(a.getUserAgent())
                .detalles(a.getDetalles())
                .build();
    }
}
