package com.abaxial.portal.events.service;

import com.abaxial.portal.events.dto.EventoDTO;
import com.abaxial.portal.events.entity.Evento;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Component
public class RecurrenciaEngine {

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ISO_LOCAL_DATE;

    /**
     * Genera las ocurrencias reales de un evento recurrente dentro del rango de visualización [desde, hasta].
     */
    public List<EventoDTO> expandirOcurrencias(
            Evento parent,
            LocalDateTime desde,
            LocalDateTime hasta,
            Set<String> fechasExcepcionSobrescritas
    ) {
        List<EventoDTO> ocurrencias = new ArrayList<>();
        if (parent == null || !"RECURRENTE".equalsIgnoreCase(parent.getTipo())) {
            return ocurrencias;
        }

        LocalDateTime serieStart = parent.getFechaInicio();
        if (serieStart == null) {
            return ocurrencias;
        }

        Duration duracion = parent.getFechaFin() != null
                ? Duration.between(serieStart, parent.getFechaFin())
                : Duration.ofHours(1);

        LocalDateTime serieEnd = parent.getFechaFinRecurrencia();

        // Rango de cálculo
        LocalDateTime windowStart = desde != null ? desde : serieStart.minusMonths(1);
        LocalDateTime windowEnd = hasta != null ? hasta : serieStart.plusYears(1);

        if (serieEnd != null && windowEnd.isAfter(serieEnd)) {
            windowEnd = serieEnd;
        }

        if (windowStart.isAfter(windowEnd)) {
            return ocurrencias;
        }

        Set<String> fechasExcluidas = parseFechasExcluidas(parent.getFechasExcluidas());
        if (fechasExcepcionSobrescritas != null) {
            fechasExcluidas.addAll(fechasExcepcionSobrescritas);
        }

        String freq = parent.getRecurrencia() != null ? parent.getRecurrencia().trim().toUpperCase() : "SEMANAL";

        switch (freq) {
            case "DIARIO":
            case "DIARIA":
                expandirDiario(parent, serieStart, windowStart, windowEnd, duracion, fechasExcluidas, ocurrencias);
                break;
            case "QUINCENAL":
                expandirQuincenal(parent, serieStart, windowStart, windowEnd, duracion, fechasExcluidas, ocurrencias);
                break;
            case "MENSUAL":
                expandirMensual(parent, serieStart, windowStart, windowEnd, duracion, fechasExcluidas, ocurrencias);
                break;
            case "ANUAL":
                expandirAnual(parent, serieStart, windowStart, windowEnd, duracion, fechasExcluidas, ocurrencias);
                break;
            case "SEMANAL":
            default:
                expandirSemanal(parent, serieStart, windowStart, windowEnd, duracion, fechasExcluidas, ocurrencias);
                break;
        }

        return ocurrencias;
    }

    private void expandirDiario(
            Evento parent,
            LocalDateTime serieStart,
            LocalDateTime windowStart,
            LocalDateTime windowEnd,
            Duration duracion,
            Set<String> fechasExcluidas,
            List<EventoDTO> result
    ) {
        LocalDate currentDay = serieStart.toLocalDate();
        if (windowStart.toLocalDate().isAfter(currentDay)) {
            currentDay = windowStart.toLocalDate();
        }

        LocalDate endDay = windowEnd.toLocalDate();

        while (!currentDay.isAfter(endDay)) {
            LocalDateTime occurrenceStart = currentDay.atTime(serieStart.toLocalTime());
            if (!occurrenceStart.isBefore(serieStart) && !occurrenceStart.isAfter(windowEnd)) {
                if (occurrenceStart.isAfter(windowStart) || occurrenceStart.isEqual(windowStart) || occurrenceStart.toLocalDate().isEqual(windowStart.toLocalDate())) {
                    String dateKey = currentDay.format(DATE_FMT);
                    if (!fechasExcluidas.contains(dateKey)) {
                        result.add(crearDTOOcurrencia(parent, occurrenceStart, duracion));
                    }
                }
            }
            currentDay = currentDay.plusDays(1);
        }
    }

    private void expandirSemanal(
            Evento parent,
            LocalDateTime serieStart,
            LocalDateTime windowStart,
            LocalDateTime windowEnd,
            Duration duracion,
            Set<String> fechasExcluidas,
            List<EventoDTO> result
    ) {
        Set<Integer> targetDaysOfWeek = parseDiasSemana(parent.getDiasSemana(), serieStart.getDayOfWeek().getValue());

        LocalDate currentDay = serieStart.toLocalDate();
        if (windowStart.toLocalDate().isAfter(currentDay)) {
            // Retroceder al inicio de la semana de windowStart para no perder ningún día
            currentDay = windowStart.toLocalDate().minusDays(windowStart.getDayOfWeek().getValue() - 1);
            if (currentDay.isBefore(serieStart.toLocalDate())) {
                currentDay = serieStart.toLocalDate();
            }
        }

        LocalDate endDay = windowEnd.toLocalDate();

        while (!currentDay.isAfter(endDay)) {
            if (targetDaysOfWeek.contains(currentDay.getDayOfWeek().getValue())) {
                LocalDateTime occurrenceStart = currentDay.atTime(serieStart.toLocalTime());
                if (!occurrenceStart.isBefore(serieStart) && !occurrenceStart.isAfter(windowEnd)) {
                    if (!occurrenceStart.isBefore(windowStart)) {
                        String dateKey = currentDay.format(DATE_FMT);
                        if (!fechasExcluidas.contains(dateKey)) {
                            result.add(crearDTOOcurrencia(parent, occurrenceStart, duracion));
                        }
                    }
                }
            }
            currentDay = currentDay.plusDays(1);
        }
    }

    private void expandirQuincenal(
            Evento parent,
            LocalDateTime serieStart,
            LocalDateTime windowStart,
            LocalDateTime windowEnd,
            Duration duracion,
            Set<String> fechasExcluidas,
            List<EventoDTO> result
    ) {
        LocalDateTime curr = serieStart;

        // Avanzar por pasos de 14 días exactos
        while (!curr.isAfter(windowEnd)) {
            if (!curr.isBefore(windowStart) && !curr.isAfter(windowEnd)) {
                String dateKey = curr.toLocalDate().format(DATE_FMT);
                if (!fechasExcluidas.contains(dateKey)) {
                    result.add(crearDTOOcurrencia(parent, curr, duracion));
                }
            }
            curr = curr.plusDays(14);
        }
    }

    private void expandirMensual(
            Evento parent,
            LocalDateTime serieStart,
            LocalDateTime windowStart,
            LocalDateTime windowEnd,
            Duration duracion,
            Set<String> fechasExcluidas,
            List<EventoDTO> result
    ) {
        int targetDay = parent.getDiaMes() != null && parent.getDiaMes() > 0 && parent.getDiaMes() <= 31
                ? parent.getDiaMes()
                : serieStart.getDayOfMonth();

        YearMonth currentYm = YearMonth.from(serieStart);
        YearMonth endYm = YearMonth.from(windowEnd);

        if (YearMonth.from(windowStart).isAfter(currentYm)) {
            currentYm = YearMonth.from(windowStart);
        }

        while (!currentYm.isAfter(endYm)) {
            int actualDay = Math.min(targetDay, currentYm.lengthOfMonth());
            LocalDate occDate = currentYm.atDay(actualDay);
            LocalDateTime occurrenceStart = occDate.atTime(serieStart.toLocalTime());

            if (!occurrenceStart.isBefore(serieStart) && !occurrenceStart.isAfter(windowEnd)) {
                if (!occurrenceStart.isBefore(windowStart)) {
                    String dateKey = occDate.format(DATE_FMT);
                    if (!fechasExcluidas.contains(dateKey)) {
                        result.add(crearDTOOcurrencia(parent, occurrenceStart, duracion));
                    }
                }
            }
            currentYm = currentYm.plusMonths(1);
        }
    }

    private void expandirAnual(
            Evento parent,
            LocalDateTime serieStart,
            LocalDateTime windowStart,
            LocalDateTime windowEnd,
            Duration duracion,
            Set<String> fechasExcluidas,
            List<EventoDTO> result
    ) {
        int startYear = serieStart.getYear();
        int endYear = windowEnd.getYear();

        for (int y = startYear; y <= endYear; y++) {
            int month = serieStart.getMonthValue();
            int maxDays = YearMonth.of(y, month).lengthOfMonth();
            int day = Math.min(serieStart.getDayOfMonth(), maxDays);
            LocalDate occDate = LocalDate.of(y, month, day);
            LocalDateTime occurrenceStart = occDate.atTime(serieStart.toLocalTime());

            if (!occurrenceStart.isBefore(serieStart) && !occurrenceStart.isAfter(windowEnd)) {
                if (!occurrenceStart.isBefore(windowStart)) {
                    String dateKey = occDate.format(DATE_FMT);
                    if (!fechasExcluidas.contains(dateKey)) {
                        result.add(crearDTOOcurrencia(parent, occurrenceStart, duracion));
                    }
                }
            }
        }
    }

    private EventoDTO crearDTOOcurrencia(Evento parent, LocalDateTime occurrenceStart, Duration duracion) {
        LocalDateTime occurrenceEnd = occurrenceStart.plus(duracion);
        String dateKey = occurrenceStart.toLocalDate().format(DATE_FMT);

        return EventoDTO.builder()
                .id(parent.getId())
                .recurrenciaId("REC-" + parent.getId() + "-" + dateKey)
                .usuarioId(parent.getUsuario() != null ? parent.getUsuario().getId() : null)
                .usuarioNombre(parent.getUsuario() != null ? parent.getUsuario().getNombreCompleto() : null)
                .clienteId(parent.getCliente() != null ? parent.getCliente().getId() : null)
                .clienteNombre(parent.getCliente() != null ? parent.getCliente().getNombre() : null)
                .titulo(parent.getTitulo())
                .descripcion(parent.getDescripcion())
                .fechaInicio(occurrenceStart)
                .fechaFin(occurrenceEnd)
                .tipo("RECURRENTE")
                .recurrencia(parent.getRecurrencia())
                .fechaFinRecurrencia(parent.getFechaFinRecurrencia())
                .diasSemana(parent.getDiasSemana())
                .diaMes(parent.getDiaMes())
                .eventoPadreId(parent.getId())
                .fechaOriginalOcurrencia(occurrenceStart)
                .esExcepcion(false)
                .esRecurrente(true)
                .prioridad(parent.getPrioridad())
                .estado(parent.getEstado())
                .visibilidad(parent.getVisibilidad())
                .activo(Boolean.TRUE.equals(parent.getActivo()))
                .fechaCreacion(parent.getFechaCreacion())
                .fechaModificacion(parent.getFechaModificacion())
                .build();
    }

    private Set<Integer> parseDiasSemana(String diasSemanaStr, int defaultDay) {
        Set<Integer> days = new HashSet<>();
        if (diasSemanaStr != null && !diasSemanaStr.isBlank()) {
            String[] parts = diasSemanaStr.split(",");
            for (String p : parts) {
                try {
                    int d = Integer.parseInt(p.trim());
                    if (d >= 1 && d <= 7) {
                        days.add(d);
                    }
                } catch (NumberFormatException ignored) {
                }
            }
        }
        if (days.isEmpty()) {
            days.add(defaultDay);
        }
        return days;
    }

    private Set<String> parseFechasExcluidas(String fechasExcluidasStr) {
        Set<String> set = new HashSet<>();
        if (fechasExcluidasStr != null && !fechasExcluidasStr.isBlank()) {
            String[] parts = fechasExcluidasStr.split(",");
            for (String p : parts) {
                String clean = p.trim();
                if (!clean.isEmpty()) {
                    set.add(clean);
                }
            }
        }
        return set;
    }
}
