package com.abaxial.portal.documents.controller;

import com.abaxial.portal.common.dto.ApiResponse;
import com.abaxial.portal.common.dto.PaginatedResponse;
import com.abaxial.portal.documents.dto.DocumentoDTO;
import com.abaxial.portal.documents.service.DocumentoService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class DocumentoController {

    private final DocumentoService documentoService;

    @GetMapping("/documentos")
    @PreAuthorize("hasAuthority('DOCUMENTO_VER') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<PaginatedResponse<DocumentoDTO>>> listarDocumentos(
            @RequestParam(required = false) Long clienteId,
            @RequestParam(required = false) String categoria,
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "false") boolean includeInactive,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        PaginatedResponse<DocumentoDTO> resultado = documentoService.listarDocumentos(clienteId, categoria, search, includeInactive, pageable);
        return ResponseEntity.ok(ApiResponse.ok(resultado));
    }

    @GetMapping("/clientes/{clienteId}/documentos")
    @PreAuthorize("hasAuthority('DOCUMENTO_VER') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<List<DocumentoDTO>>> listarPorCliente(
            @PathVariable Long clienteId,
            @RequestParam(defaultValue = "true") boolean soloActivos
    ) {
        List<DocumentoDTO> docs = documentoService.listarPorCliente(clienteId, soloActivos);
        return ResponseEntity.ok(ApiResponse.ok(docs));
    }

    @GetMapping("/documentos/{id}")
    @PreAuthorize("hasAuthority('DOCUMENTO_VER') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<DocumentoDTO>> obtenerPorId(@PathVariable Long id) {
        DocumentoDTO doc = documentoService.obtenerPorId(id);
        return ResponseEntity.ok(ApiResponse.ok(doc));
    }

    @PostMapping(value = "/clientes/{clienteId}/documentos", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAuthority('DOCUMENTO_SUBIR') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<DocumentoDTO>> subirDocumento(
            @PathVariable Long clienteId,
            @RequestParam(required = false) String categoria,
            @RequestParam(required = false) String descripcion,
            @RequestPart("file") MultipartFile file,
            Authentication auth
    ) {
        DocumentoDTO creado = documentoService.subirDocumento(clienteId, categoria, descripcion, file, auth.getName());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Documento subido exitosamente", creado));
    }

    @PostMapping(value = "/documentos/{id}/versiones", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAuthority('DOCUMENTO_SUBIR') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<DocumentoDTO>> agregarNuevaVersion(
            @PathVariable Long id,
            @RequestPart("file") MultipartFile file,
            Authentication auth
    ) {
        DocumentoDTO actualizado = documentoService.agregarNuevaVersion(id, file, auth.getName());
        return ResponseEntity.ok(ApiResponse.ok("Nueva versión registrada exitosamente", actualizado));
    }

    @GetMapping("/documentos/{id}/download")
    @PreAuthorize("hasAuthority('DOCUMENTO_DESCARGAR') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<Resource> descargarDocumento(
            @PathVariable Long id,
            @RequestParam(required = false) Integer version,
            Authentication auth
    ) {
        DocumentoService.DownloadFileInfo fileInfo = documentoService.prepararDescarga(id, version, auth.getName());

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(fileInfo.mimeType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileInfo.filename() + "\"")
                .header(HttpHeaders.CONTENT_LENGTH, String.valueOf(fileInfo.size()))
                .body(fileInfo.resource());
    }

    @DeleteMapping("/documentos/{id}")
    @PreAuthorize("hasAuthority('DOCUMENTO_ELIMINAR') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<Void>> desactivarDocumento(
            @PathVariable Long id,
            Authentication auth
    ) {
        documentoService.desactivarDocumento(id, auth.getName());
        return ResponseEntity.ok(ApiResponse.ok("Documento desactivado correctamente", null));
    }
}
