package com.abaxial.portal.clients.dto;

import com.abaxial.portal.clients.entity.Cliente;
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
public class ClienteListDTO {
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
    private int totalEquipos;
    private int totalServicios;
    private int totalWebs;
    private int totalDocumentos;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaModificacion;

    public static ClienteListDTO fromEntity(Cliente c) {
        int eqCount = c.getEquipos() != null ? (int) c.getEquipos().stream().filter(e -> Boolean.TRUE.equals(e.getActivo())).count() : 0;
        int srvCount = c.getServicios() != null ? (int) c.getServicios().stream().filter(s -> Boolean.TRUE.equals(s.getActivo())).count() : 0;
        int webCount = c.getWebs() != null ? (int) c.getWebs().stream().filter(w -> Boolean.TRUE.equals(w.getActivo())).count() : 0;
        int docCount = c.getDocumentos() != null ? (int) c.getDocumentos().stream().filter(d -> Boolean.TRUE.equals(d.getActivo())).count() : 0;

        return ClienteListDTO.builder()
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
                .totalEquipos(eqCount)
                .totalServicios(srvCount)
                .totalWebs(webCount)
                .totalDocumentos(docCount)
                .fechaCreacion(c.getFechaCreacion())
                .fechaModificacion(c.getFechaModificacion())
                .build();
    }
}
