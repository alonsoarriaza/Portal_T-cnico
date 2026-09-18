package com.abaxial.portal.clients.importer.controller;

import com.abaxial.portal.common.dto.ApiResponse;
import com.abaxial.portal.clients.importer.dto.ImportPlanDTO;
import com.abaxial.portal.clients.importer.dto.ImportSummaryDTO;
import com.abaxial.portal.clients.importer.service.ClientImportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/clientes/importar")
@RequiredArgsConstructor
public class ClientImportController {

    private final ClientImportService clientImportService;

    @PostMapping(value = "/analizar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAuthority('CLIENTE_CREAR') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<ImportPlanDTO>> analizarDocumento(
            @RequestPart("file") MultipartFile file
    ) {
        ImportPlanDTO plan = clientImportService.analizar(file);
        return ResponseEntity.ok(ApiResponse.ok("Documento analizado correctamente", plan));
    }

    @PostMapping(value = "/ejecutar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAuthority('CLIENTE_CREAR') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<ImportSummaryDTO>> ejecutarImportacion(
            @RequestPart("file") MultipartFile file,
            Authentication auth
    ) {
        ImportSummaryDTO summary = clientImportService.ejecutar(file, auth.getName());
        return ResponseEntity.ok(ApiResponse.ok("Importación completada con éxito", summary));
    }
}
