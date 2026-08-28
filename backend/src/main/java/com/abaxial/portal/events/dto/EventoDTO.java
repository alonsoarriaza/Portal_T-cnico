package com.abaxial.portal.events.dto;

import com.abaxial.portal.events.entity.Evento;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventoDTO {
    private Long id;
    private String recurrenciaId;
    private Long usuarioId;
    private String usuarioNombre;
    private Long clienteId;
    private String clienteNombre;
    private String titulo;
    private String descripcion;
    private LocalDateTime fechaInicio;
    private LocalDateTime fechaFin;
    private String tipo;
    private String recurrencia;
    private LocalDateTime fechaFinRecurrencia;
    private String diasSemana;
    private Integer diaMes;
    private Long eventoPadreId;
    private LocalDateTime fechaOriginalOcurrencia;
    private boolean esExcepcion;
    private boolean esRecurrente;
    private String prioridad;
    private String estado;
    private String visibilidad;
    private boolean activo;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaModificacion;

    public static EventoDTO fromEntity(Evento e) {
        boolean isRec = "RECURRENTE".equalsIgnoreCase(e.getTipo());
        return EventoDTO.builder()
                .id(e.getId())
                .recurrenciaId(e.getId() != null ? e.getId().toString() : null)
                .usuarioId(e.getUsuario() != null ? e.getUsuario().getId() : null)
                .usuarioNombre(e.getUsuario() != null ? e.getUsuario().getNombreCompleto() : null)
                .clienteId(e.getCliente() != null ? e.getCliente().getId() : null)
                .clienteNombre(e.getCliente() != null ? e.getCliente().getNombre() : null)
                .titulo(e.getTitulo())
                .descripcion(e.getDescripcion())
                .fechaInicio(e.getFechaInicio())
                .fechaFin(e.getFechaFin())
                .tipo(e.getTipo())
                .recurrencia(e.getRecurrencia())
                .fechaFinRecurrencia(e.getFechaFinRecurrencia())
                .diasSemana(e.getDiasSemana())
                .diaMes(e.getDiaMes())
                .eventoPadreId(e.getEventoPadreId())
                .fechaOriginalOcurrencia(e.getFechaOriginalOcurrencia())
                .esExcepcion(Boolean.TRUE.equals(e.getEsExcepcion()))
                .esRecurrente(isRec)
                .prioridad(e.getPrioridad())
                .estado(e.getEstado())
                .visibilidad(e.getVisibilidad())
                .activo(Boolean.TRUE.equals(e.getActivo()))
                .fechaCreacion(e.getFechaCreacion())
                .fechaModificacion(e.getFechaModificacion())
                .build();
    }
}
