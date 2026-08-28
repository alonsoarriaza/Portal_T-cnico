package com.abaxial.portal.clients.dto;

import com.abaxial.portal.audit.dto.AuditoriaDTO;
import com.abaxial.portal.events.dto.EventoDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardStatsDTO {
    private long totalClientes;
    private long clientesActivos;
    private long clientesInactivos;
    private long totalEquipos;
    private long totalServicios;
    private long totalWebs;
    private long totalDocumentos;
    private long totalUsuarios;
    private long eventosPendientes;
    private List<EventoDTO> proximosEventos;
    private List<AuditoriaDTO> actividadReciente;
}
