package com.abaxial.portal.equipment.dto;

import com.abaxial.portal.equipment.entity.Equipo;
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
public class EquipoDTO {
    private Long id;
    private Long clienteId;
    private String clienteNombre;
    private String codigoInventario;
    private String tipo;
    private String marca;
    private String modelo;
    private String numeroSerie;
    private String estado;
    private LocalDate fechaAlta;
    private LocalDate fechaBaja;
    private String observaciones;
    private boolean activo;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaModificacion;

    public static EquipoDTO fromEntity(Equipo e) {
        return EquipoDTO.builder()
                .id(e.getId())
                .clienteId(e.getCliente() != null ? e.getCliente().getId() : null)
                .clienteNombre(e.getCliente() != null ? e.getCliente().getNombre() : null)
                .codigoInventario(e.getCodigoInventario())
                .tipo(e.getTipo())
                .marca(e.getMarca())
                .modelo(e.getModelo())
                .numeroSerie(e.getNumeroSerie())
                .estado(e.getEstado())
                .fechaAlta(e.getFechaAlta())
                .fechaBaja(e.getFechaBaja())
                .observaciones(e.getObservaciones())
                .activo(Boolean.TRUE.equals(e.getActivo()))
                .fechaCreacion(e.getFechaCreacion())
                .fechaModificacion(e.getFechaModificacion())
                .build();
    }
}
