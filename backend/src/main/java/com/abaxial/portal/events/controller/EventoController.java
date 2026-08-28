package com.abaxial.portal.events.controller;

import com.abaxial.portal.common.dto.ApiResponse;
import com.abaxial.portal.events.dto.EventoDTO;
import com.abaxial.portal.events.dto.EventoRequestDTO;
import com.abaxial.portal.events.service.EventoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/eventos")
@RequiredArgsConstructor
public class EventoController {

    private final EventoService eventoService;

    @GetMapping
    @PreAuthorize("hasAuthority('CRONOGRAMA_VER') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<List<EventoDTO>>> obtenerEventos(
            @RequestParam(required = false) Long usuarioId,
            @RequestParam(required = false) Long clienteId,
            @RequestParam(required = false) String estado,
            @RequestParam(required = false) String tipo,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime desde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime hasta,
            @RequestParam(defaultValue = "false") boolean includeInactive,
            Authentication auth
    ) {
        List<EventoDTO> eventos = eventoService.obtenerEventosCalendario(
                usuarioId, clienteId, estado, tipo, desde, hasta, includeInactive, auth.getName()
        );
        return ResponseEntity.ok(ApiResponse.ok(eventos));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('CRONOGRAMA_VER') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<EventoDTO>> obtenerEvento(
            @PathVariable Long id,
            Authentication auth
    ) {
        EventoDTO evento = eventoService.obtenerPorId(id, auth.getName());
        return ResponseEntity.ok(ApiResponse.ok(evento));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('CRONOGRAMA_GESTIONAR') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<EventoDTO>> crearEvento(
            @Valid @RequestBody EventoRequestDTO request,
            Authentication auth
    ) {
        EventoDTO creado = eventoService.crearEvento(request, auth.getName());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Evento agendado exitosamente", creado));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('CRONOGRAMA_GESTIONAR') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<EventoDTO>> actualizarEvento(
            @PathVariable Long id,
            @Valid @RequestBody EventoRequestDTO request,
            Authentication auth
    ) {
        EventoDTO actualizado = eventoService.actualizarEvento(id, request, auth.getName());
        return ResponseEntity.ok(ApiResponse.ok("Evento actualizado correctamente", actualizado));
    }

    @PostMapping("/{id}/excepcion")
    @PreAuthorize("hasAuthority('CRONOGRAMA_GESTIONAR') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<EventoDTO>> modificarExcepcion(
            @PathVariable Long id,
            @Valid @RequestBody EventoRequestDTO request,
            Authentication auth
    ) {
        EventoDTO excepcion = eventoService.crearOActualizarExcepcion(id, request, auth.getName());
        return ResponseEntity.ok(ApiResponse.ok("Instancia del evento modificada correctamente", excepcion));
    }

    @DeleteMapping("/{id}/excepcion")
    @PreAuthorize("hasAuthority('CRONOGRAMA_GESTIONAR') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<Void>> excluirOcurrencia(
            @PathVariable Long id,
            @RequestParam String fechaOriginal,
            Authentication auth
    ) {
        eventoService.excluirOcurrencia(id, fechaOriginal, auth.getName());
        return ResponseEntity.ok(ApiResponse.ok("Ocurrencia eliminada de la serie correctamente", null));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('CRONOGRAMA_GESTIONAR') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<Void>> desactivarEvento(
            @PathVariable Long id,
            Authentication auth
    ) {
        eventoService.desactivarEvento(id, auth.getName());
        return ResponseEntity.ok(ApiResponse.ok("Evento cancelado/desactivado correctamente", null));
    }
}
