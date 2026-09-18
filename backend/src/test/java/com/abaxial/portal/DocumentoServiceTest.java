package com.abaxial.portal;

import com.abaxial.portal.clients.dto.ClienteDetailDTO;
import com.abaxial.portal.clients.dto.ClienteRequestDTO;
import com.abaxial.portal.clients.service.ClienteService;
import com.abaxial.portal.common.exception.DocumentConflictException;
import com.abaxial.portal.documents.dto.DocumentoDTO;
import com.abaxial.portal.documents.service.DocumentoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class DocumentoServiceTest {

    @Autowired
    private DocumentoService documentoService;

    @Autowired
    private ClienteService clienteService;

    private Long clienteId;

    @BeforeEach
    void setUp() {
        ClienteRequestDTO clienteReq = ClienteRequestDTO.builder()
                .nifCif("B12399999")
                .nombre("Empresa Documental Test")
                .build();
        ClienteDetailDTO cliente = clienteService.crearCliente(clienteReq, "admin");
        this.clienteId = cliente.getId();
    }

    @Test
    void testSubirDocumentoYDetectarConflicto() {
        MockMultipartFile file1 = new MockMultipartFile(
                "file",
                "Contrato_Mantenimiento.pdf",
                "application/pdf",
                "Contenido de prueba v1".getBytes()
        );

        DocumentoDTO docV1 = documentoService.subirDocumento(
                clienteId,
                "CONTRATOS",
                "Contrato de soporte 2026",
                file1,
                "admin"
        );

        assertNotNull(docV1.getId());
        assertEquals("Contrato_Mantenimiento.pdf", docV1.getNombreOriginal());
        assertEquals("GENERAL", docV1.getCategoria());
        assertEquals(1, docV1.getVersionActual());
        assertEquals(1, docV1.getVersiones().size());

        // Intentar subir de nuevo con el MISMO nombre -> debe lanzar DocumentConflictException
        MockMultipartFile fileConflict = new MockMultipartFile(
                "file",
                "Contrato_Mantenimiento.pdf",
                "application/pdf",
                "Contenido modificado".getBytes()
        );

        DocumentConflictException ex = assertThrows(DocumentConflictException.class, () ->
                documentoService.subirDocumento(clienteId, "CONTRATOS", "Nuevo intento", fileConflict, "admin")
        );

        assertEquals(docV1.getId(), ex.getExistingDocumentId());
        assertEquals(1, ex.getCurrentVersion());

        // Subir como nueva versión v2
        DocumentoDTO docV2 = documentoService.agregarNuevaVersion(docV1.getId(), fileConflict, "admin");
        assertEquals(2, docV2.getVersionActual());
        assertEquals(2, docV2.getVersiones().size());
        assertEquals(2, docV2.getUltimaVersion().getVersion());
    }

    @Test
    void testSubirDocumentosMultiplesFuerzaEstadoGeneral() {
        MockMultipartFile fileA = new MockMultipartFile(
                "file",
                "Factura_Septiembre.pdf",
                "application/pdf",
                "Factura A".getBytes()
        );
        MockMultipartFile fileB = new MockMultipartFile(
                "file",
                "Informe_Red.pdf",
                "application/pdf",
                "Informe B".getBytes()
        );

        java.util.List<DocumentoDTO> batch = documentoService.subirDocumentosMultiples(
                clienteId,
                "FACTURAS_O_CONTRATOS",
                "Subida lote",
                java.util.List.of(fileA, fileB),
                "admin"
        );

        assertEquals(2, batch.size());
        for (DocumentoDTO d : batch) {
            assertEquals("GENERAL", d.getCategoria());
        }
    }
}
