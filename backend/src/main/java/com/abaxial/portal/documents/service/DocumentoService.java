package com.abaxial.portal.documents.service;

import com.abaxial.portal.audit.service.AuditoriaService;
import com.abaxial.portal.clients.entity.Cliente;
import com.abaxial.portal.clients.repository.ClienteRepository;
import com.abaxial.portal.common.dto.PaginatedResponse;
import com.abaxial.portal.common.exception.BadRequestException;
import com.abaxial.portal.common.exception.DocumentConflictException;
import com.abaxial.portal.common.exception.ResourceNotFoundException;
import com.abaxial.portal.documents.dto.DocumentoDTO;
import com.abaxial.portal.documents.dto.DocumentoVersionDTO;
import com.abaxial.portal.documents.entity.Documento;
import com.abaxial.portal.documents.entity.DocumentoVersion;
import com.abaxial.portal.documents.repository.DocumentoRepository;
import com.abaxial.portal.documents.repository.DocumentoVersionRepository;
import com.abaxial.portal.documents.storage.FileStorageService;
import com.abaxial.portal.users.entity.Usuario;
import com.abaxial.portal.users.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DocumentoService {

    private static final Set<String> ALLOWED_EXTENSIONS = Set.of(
            "docx", "doc", "pdf", "xlsx", "xls", "pptx", "ppt", "txt",
            "jpg", "jpeg", "png", "webp", "zip", "rar", "7z", "csv", "xml", "json"
    );

    private static final long MAX_FILE_SIZE = 50 * 1024 * 1024; // 50 MB

    private final DocumentoRepository documentoRepository;
    private final DocumentoVersionRepository versionRepository;
    private final ClienteRepository clienteRepository;
    private final UsuarioRepository usuarioRepository;
    private final FileStorageService storageService;
    private final AuditoriaService auditoriaService;

    @Transactional(readOnly = true)
    public PaginatedResponse<DocumentoDTO> listarDocumentos(
            Long clienteId, String categoria, String search,
            boolean includeInactive, Pageable pageable
    ) {
        Page<Documento> page = documentoRepository.findAllFiltered(clienteId, categoria, search, includeInactive, pageable);
        return PaginatedResponse.from(page.map(DocumentoDTO::fromEntity));
    }

    @Transactional(readOnly = true)
    public List<DocumentoDTO> listarPorCliente(Long clienteId, boolean soloActivos) {
        List<Documento> list = soloActivos
                ? documentoRepository.findByClienteIdAndActivoTrueOrderByFechaCreacionDesc(clienteId)
                : documentoRepository.findByClienteIdOrderByFechaCreacionDesc(clienteId);
        return list.stream().map(DocumentoDTO::fromEntity).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public DocumentoDTO obtenerPorId(Long id) {
        Documento d = documentoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Documento no encontrado con ID: " + id));
        return DocumentoDTO.fromEntity(d);
    }

    @Transactional
    public DocumentoDTO subirDocumento(
            Long clienteId, String categoria, String descripcion,
            MultipartFile file, String currentUsername
    ) {
        validateFile(file);

        Cliente cliente = clienteRepository.findById(clienteId)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado con ID: " + clienteId));

        String originalFilename = StringUtils.cleanPath(file.getOriginalFilename() != null ? file.getOriginalFilename() : "documento");

        // Regla: Detectar conflicto si ya existe documento activo con el mismo nombre
        Optional<Documento> docExistente = documentoRepository.findByClienteIdAndNombreOriginalAndActivoTrue(clienteId, originalFilename);
        if (docExistente.isPresent()) {
            Documento existente = docExistente.get();
            throw new DocumentConflictException(
                    "Ya existe un documento con el nombre '" + originalFilename + "' para este cliente. Puede subir una nueva versión o renombrar el archivo.",
                    existente.getId(),
                    existente.getNombreOriginal(),
                    existente.getVersionActual()
            );
        }

        Usuario usuario = usuarioRepository.findByUsername(currentUsername).orElse(null);

        // Crear contenedor Documento
        Documento documento = Documento.builder()
                .cliente(cliente)
                .nombreOriginal(originalFilename)
                .categoria(categoria != null && !categoria.isBlank() ? categoria : "GENERAL")
                .descripcion(descripcion)
                .versionActual(1)
                .activo(true)
                .fechaCreacion(LocalDateTime.now())
                .fechaModificacion(LocalDateTime.now())
                .build();

        Documento docGuardado = documentoRepository.save(documento);

        // Guardar Versión 1 en storage y BBDD
        DocumentoVersion version1 = createAndStoreVersion(docGuardado, 1, file, usuario);
        docGuardado.getVersiones().add(version1);

        auditoriaService.registrarAsync(
                currentUsername,
                "DOCUMENTO_SUBIDO",
                "Documento",
                docGuardado.getId(),
                "Documento subido (v1): " + originalFilename + " (" + formatFileSize(file.getSize()) + ") para Cliente " + cliente.getNombre()
        );

        return DocumentoDTO.fromEntity(docGuardado);
    }

    @Transactional
    public DocumentoDTO agregarNuevaVersion(
            Long documentoId, MultipartFile file, String currentUsername
    ) {
        validateFile(file);

        Documento documento = documentoRepository.findById(documentoId)
                .orElseThrow(() -> new ResourceNotFoundException("Documento no encontrado con ID: " + documentoId));

        Usuario usuario = usuarioRepository.findByUsername(currentUsername).orElse(null);
        int nuevaVersionNum = documento.getVersionActual() + 1;

        DocumentoVersion nuevaVersion = createAndStoreVersion(documento, nuevaVersionNum, file, usuario);
        documento.setVersionActual(nuevaVersionNum);
        documento.setFechaModificacion(LocalDateTime.now());
        documento.getVersiones().add(0, nuevaVersion);

        Documento docActualizado = documentoRepository.save(documento);

        auditoriaService.registrarAsync(
                currentUsername,
                "DOCUMENTO_NUEVA_VERSION",
                "Documento",
                docActualizado.getId(),
                "Nueva versión (v" + nuevaVersionNum + ") subida para: " + documento.getNombreOriginal() + " (" + formatFileSize(file.getSize()) + ")"
        );

        return DocumentoDTO.fromEntity(docActualizado);
    }

    @Transactional(readOnly = true)
    public DownloadFileInfo prepararDescarga(Long documentoId, Integer versionNum, String currentUsername) {
        Documento documento = documentoRepository.findById(documentoId)
                .orElseThrow(() -> new ResourceNotFoundException("Documento no encontrado con ID: " + documentoId));

        DocumentoVersion version;
        if (versionNum != null) {
            version = versionRepository.findByDocumentoIdAndVersion(documentoId, versionNum)
                    .orElseThrow(() -> new ResourceNotFoundException("Versión " + versionNum + " no encontrada para el documento"));
        } else {
            version = versionRepository.findByDocumentoIdAndVersion(documentoId, documento.getVersionActual())
                    .orElseThrow(() -> new ResourceNotFoundException("Versión actual no encontrada"));
        }

        Resource resource = storageService.loadAsResource(version.getRutaStorage());

        auditoriaService.registrarAsync(
                currentUsername,
                "DOCUMENTO_DESCARGADO",
                "Documento",
                documento.getId(),
                "Descargada versión " + version.getVersion() + " de " + version.getNombreArchivo()
        );

        return new DownloadFileInfo(resource, version.getNombreArchivo(), version.getMimeType(), version.getTamano());
    }

    @Transactional
    public void desactivarDocumento(Long id, String currentUsername) {
        Documento documento = documentoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Documento no encontrado con ID: " + id));

        documento.setActivo(false);
        documento.setFechaEliminacion(LocalDateTime.now());
        usuarioRepository.findByUsername(currentUsername).ifPresent(u -> documento.setEliminadoPor(u.getId()));
        documentoRepository.save(documento);

        auditoriaService.registrarAsync(
                currentUsername,
                "DOCUMENTO_DESACTIVADO",
                "Documento",
                documento.getId(),
                "Documento desactivado (soft delete): " + documento.getNombreOriginal()
        );
    }

    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BadRequestException("El archivo es obligatorio.");
        }
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new BadRequestException("El archivo excede el tamaño máximo permitido de 50 MB.");
        }
        String filename = StringUtils.cleanPath(file.getOriginalFilename() != null ? file.getOriginalFilename() : "");
        String ext = getFileExtension(filename);
        if (!ext.isEmpty() && !ALLOWED_EXTENSIONS.contains(ext)) {
            throw new BadRequestException("Formato de archivo no permitido (." + ext + "). Formatos admitidos: DOCX, PDF, XLSX, PPTX, TXT, imágenes, ZIP, CSV.");
        }
    }

    private DocumentoVersion createAndStoreVersion(Documento doc, int versionNum, MultipartFile file, Usuario usuario) {
        String filename = StringUtils.cleanPath(file.getOriginalFilename() != null ? file.getOriginalFilename() : "archivo");
        String extension = getFileExtension(filename);
        String nombreInterno = UUID.randomUUID() + (extension.isEmpty() ? "" : "." + extension);

        String relativePath = storageService.storeFile(doc.getCliente().getId(), nombreInterno, file);

        DocumentoVersion version = DocumentoVersion.builder()
                .documento(doc)
                .version(versionNum)
                .nombreArchivo(filename)
                .nombreInterno(nombreInterno)
                .extension(extension)
                .mimeType(file.getContentType() != null ? file.getContentType() : "application/octet-stream")
                .tamano(file.getSize())
                .rutaStorage(relativePath)
                .usuario(usuario)
                .fechaSubida(LocalDateTime.now())
                .build();

        return versionRepository.save(version);
    }

    private String getFileExtension(String filename) {
        int dotIndex = filename.lastIndexOf('.');
        return (dotIndex > 0 && dotIndex < filename.length() - 1) ? filename.substring(dotIndex + 1).toLowerCase() : "";
    }

    private String formatFileSize(long bytes) {
        if (bytes < 1024) return bytes + " B";
        int exp = (int) (Math.log(bytes) / Math.log(1024));
        char pre = "KMGTPE".charAt(exp - 1);
        return String.format("%.1f %sB", bytes / Math.pow(1024, exp), pre);
    }

    public record DownloadFileInfo(Resource resource, String filename, String mimeType, long size) {}
}
