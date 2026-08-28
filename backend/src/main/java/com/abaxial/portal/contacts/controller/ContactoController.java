package com.abaxial.portal.contacts.controller;

import com.abaxial.portal.common.dto.ApiResponse;
import com.abaxial.portal.contacts.dto.ContactoDTO;
import com.abaxial.portal.contacts.dto.ContactoRequestDTO;
import com.abaxial.portal.contacts.service.ContactoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ContactoController {

    private final ContactoService contactoService;

    @GetMapping("/clientes/{clienteId}/contactos")
    @PreAuthorize("hasAuthority('CONTACTO_VER') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<List<ContactoDTO>>> listarPorCliente(
            @PathVariable Long clienteId,
            @RequestParam(defaultValue = "true") boolean soloActivos
    ) {
        List<ContactoDTO> contactos = contactoService.listarPorCliente(clienteId, soloActivos);
        return ResponseEntity.ok(ApiResponse.ok(contactos));
    }

    @PostMapping("/clientes/{clienteId}/contactos")
    @PreAuthorize("hasAuthority('CONTACTO_GESTIONAR') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<ContactoDTO>> crearContacto(
            @PathVariable Long clienteId,
            @Valid @RequestBody ContactoRequestDTO request,
            Authentication auth
    ) {
        ContactoDTO creado = contactoService.crearContacto(clienteId, request, auth.getName());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Contacto creado exitosamente", creado));
    }

    @PutMapping("/contactos/{id}")
    @PreAuthorize("hasAuthority('CONTACTO_GESTIONAR') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<ContactoDTO>> actualizarContacto(
            @PathVariable Long id,
            @Valid @RequestBody ContactoRequestDTO request,
            Authentication auth
    ) {
        ContactoDTO actualizado = contactoService.actualizarContacto(id, request, auth.getName());
        return ResponseEntity.ok(ApiResponse.ok("Contacto actualizado correctamente", actualizado));
    }

    @DeleteMapping("/contactos/{id}")
    @PreAuthorize("hasAuthority('CONTACTO_GESTIONAR') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<Void>> desactivarContacto(
            @PathVariable Long id,
            Authentication auth
    ) {
        contactoService.desactivarContacto(id, auth.getName());
        return ResponseEntity.ok(ApiResponse.ok("Contacto desactivado correctamente", null));
    }
}
