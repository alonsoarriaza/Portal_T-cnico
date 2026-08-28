package com.abaxial.portal.websites.dto;

import com.abaxial.portal.websites.entity.Web;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WebDTO {
    private Long id;
    private Long clienteId;
    private String clienteNombre;
    private String nombre;
    private String url;
    private String estado;
    private String descripcion;
    private LocalDate fechaRegistro;
    private String observaciones;
    private boolean activo;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaModificacion;

    public static WebDTO fromEntity(Web w) {
        return WebDTO.builder()
                .id(w.getId())
                .clienteId(w.getCliente() != null ? w.getCliente().getId() : null)
                .clienteNombre(w.getCliente() != null ? w.getCliente().getNombre() : null)
                .nombre(w.getNombre())
                .url(w.getUrl())
                .estado(w.getEstado())
                .descripcion(w.getDescripcion())
                .fechaRegistro(w.getFechaRegistro())
                .observaciones(w.getObservaciones())
                .activo(Boolean.TRUE.equals(w.getActivo()))
                .fechaCreacion(w.getFechaCreacion())
                .fechaModificacion(w.getFechaModificacion())
                .build();
    }
}
