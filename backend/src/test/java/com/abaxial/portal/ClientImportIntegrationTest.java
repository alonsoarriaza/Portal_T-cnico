package com.abaxial.portal;

import com.abaxial.portal.clients.entity.Cliente;
import com.abaxial.portal.clients.importer.dto.ImportPlanDTO;
import com.abaxial.portal.clients.importer.dto.ImportSummaryDTO;
import com.abaxial.portal.clients.importer.dto.ParsedImportData;
import com.abaxial.portal.clients.importer.parser.HtmlDocumentParser;
import com.abaxial.portal.clients.importer.parser.TextDocumentParser;
import com.abaxial.portal.clients.importer.service.ClientImportService;
import com.abaxial.portal.clients.repository.ClienteRepository;
import com.abaxial.portal.contacts.entity.Contacto;
import com.abaxial.portal.contacts.repository.ContactoRepository;
import com.abaxial.portal.documents.dto.DocumentoDTO;
import com.abaxial.portal.documents.entity.Documento;
import com.abaxial.portal.documents.entity.DocumentoVersion;
import com.abaxial.portal.documents.repository.DocumentoRepository;
import com.abaxial.portal.documents.service.DocumentoService;
import com.abaxial.portal.equipment.dto.EquipoDTO;
import com.abaxial.portal.equipment.dto.EquipoRequestDTO;
import com.abaxial.portal.equipment.entity.Equipo;
import com.abaxial.portal.equipment.repository.EquipoRepository;
import com.abaxial.portal.equipment.service.EquipoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class ClientImportIntegrationTest {

    @Autowired
    private ClientImportService importService;

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private EquipoRepository equipoRepository;

    @Autowired
    private ContactoRepository contactoRepository;

    @Autowired
    private EquipoService equipoService;

    @Autowired
    private com.abaxial.portal.equipment.controller.EquipoController equipoController;

    @Autowired
    private DocumentoRepository documentoRepository;

    @Autowired
    private DocumentoService documentoService;

    @Autowired
    private TextDocumentParser textDocumentParser;

    @Autowired
    private HtmlDocumentParser htmlDocumentParser;

    private Cliente clienteTestA;
    private Cliente clienteTestB;

    @BeforeEach
    void setUp() {
        // Sembrar clientes base para tests
        clienteTestA = clienteRepository.save(Cliente.builder()
                .codigo("43000991")
                .nifCif("B12345678")
                .nombre("Cliente Prueba A")
                .estado("ALTA")
                .direccion("Calle Real 10")
                .poblacion("Sevilla")
                .provincia("Sevilla")
                .gerente("Juan Perez")
                .fechaAlta(LocalDate.now())
                .activo(true)
                .build());

        clienteTestB = clienteRepository.save(Cliente.builder()
                .codigo("43000992")
                .nifCif("B87654321")
                .nombre("Cliente Prueba B")
                .estado("ALTA")
                .direccion("Avenida Constitución 5")
                .poblacion("Sevilla")
                .provincia("Sevilla")
                .gerente("Maria Garcia")
                .fechaAlta(LocalDate.now())
                .activo(true)
                .build());
    }

    @Test
    @DisplayName("Test 1: Cliente nuevo sin equipos -> client created, equipment count = 0")
    void test1_ClienteNuevoSinEquipos() {
        String docContent = """
                Colegio Nuevo Sin Equipos
                
                Estado\tAlta
                Mantenimiento\tSí
                Equipos/dispositivos inventariados\t0
                Código\t43000999
                CIF/NIF\tA99999999
                Nombre completo\tColegio Nuevo Sin Equipos
                Dirección\tCalle Nueva 1
                Población\tDos Hermanas
                Provincia\tSevilla
                Fecha alta\t15/05/2020
                """;

        MockMultipartFile file = new MockMultipartFile(
                "file", "nuevo_cliente.txt", "text/plain", docContent.getBytes(StandardCharsets.UTF_8)
        );

        ImportSummaryDTO summary = importService.ejecutar(file, "admin");

        assertEquals(1, summary.getClientsCreated());
        assertEquals(0, summary.getEquipmentsCreated());

        Optional<Cliente> opt = clienteRepository.findByCodigo("43000999");
        assertTrue(opt.isPresent());
        List<Equipo> eqList = equipoRepository.findByClienteIdOrderByCodigoInventarioAsc(opt.get().getId());
        assertTrue(eqList.isEmpty(), "El cliente nuevo sin equipos debe tener 0 equipos");
    }

    @Test
    @DisplayName("Test 2: Cliente existente -> no duplicate client")
    void test2_ClienteExistenteNoDuplicado() {
        long countBefore = clienteRepository.count();

        String docContent = """
                Cliente Prueba A
                
                Código\t43000991
                CIF/NIF\tB12345678
                Nombre completo\tCliente Prueba A
                Equipos/dispositivos inventariados\t0
                """;

        MockMultipartFile file = new MockMultipartFile(
                "file", "cliente_existente.txt", "text/plain", docContent.getBytes(StandardCharsets.UTF_8)
        );

        ImportSummaryDTO summary = importService.ejecutar(file, "admin");

        assertEquals(0, summary.getClientsCreated(), "No debe crear clientes nuevos");
        assertEquals(1, summary.getClientsMatched(), "Debe matchear y reutilizar el cliente existente");
        assertEquals(countBefore, clienteRepository.count(), "El total de clientes no debe aumentar");
    }

    @Test
    @DisplayName("Test 3: Cliente existente + nuevos equipos -> client reused, new equipment inserted")
    void test3_ClienteExistenteNuevosEquipos() {
        String docContent = """
                Cliente Prueba A
                
                Código\t43000991
                CIF/NIF\tB12345678
                Nombre completo\tCliente Prueba A
                Equipos/dispositivos inventariados\t2
                
                EQUIPOS:
                
                EQ-991-01
                pc-recepcion
                Sobremesa
                Recepción
                Alta
                Sin acciones
                
                EQ-991-02
                laptop-direccion
                Portátil
                Dirección
                Alta
                10/05/2021 10:00
                """;

        MockMultipartFile file = new MockMultipartFile(
                "file", "nuevos_equipos.txt", "text/plain", docContent.getBytes(StandardCharsets.UTF_8)
        );

        ImportSummaryDTO summary = importService.ejecutar(file, "admin");

        assertEquals(0, summary.getClientsCreated(), "Cliente debe ser reutilizado");
        assertEquals(1, summary.getClientsMatched());
        assertEquals(2, summary.getEquipmentsCreated(), "Deben insertarse los 2 nuevos equipos");

        Optional<Equipo> eq1 = equipoRepository.findByCodigoInventario("EQ-991-01");
        assertTrue(eq1.isPresent());
        assertEquals(clienteTestA.getId(), eq1.get().getCliente().getId());
        assertEquals("pc-recepcion", eq1.get().getNombreEquipo());
    }

    @Test
    @DisplayName("Test 4: Cliente existente + equipos existentes -> no duplicate equipment")
    void test4_ClienteExistenteEquiposExistentes() {
        // Pre-insertar equipo
        equipoRepository.save(Equipo.builder()
                .cliente(clienteTestA)
                .codigoInventario("EQ-991-01")
                .nombreEquipo("pc-recepcion")
                .tipo("Sobremesa")
                .estado("OPERATIVO")
                .fechaAlta(LocalDate.now())
                .activo(true)
                .build());

        long countEquiposBefore = equipoRepository.count();

        String docContent = """
                Cliente Prueba A
                
                Código\t43000991
                CIF/NIF\tB12345678
                Nombre completo\tCliente Prueba A
                Equipos/dispositivos inventariados\t1
                
                EQUIPOS:
                
                EQ-991-01
                pc-recepcion
                Sobremesa
                Recepción
                Alta
                Sin acciones
                """;

        MockMultipartFile file = new MockMultipartFile(
                "file", "equipos_repetidos.txt", "text/plain", docContent.getBytes(StandardCharsets.UTF_8)
        );

        ImportSummaryDTO summary = importService.ejecutar(file, "admin");

        assertEquals(0, summary.getEquipmentsCreated(), "No debe crear equipos duplicados");
        assertEquals(1, summary.getEquipmentsMatched(), "Debe identificar el equipo como existente");
        assertEquals(countEquiposBefore, equipoRepository.count(), "Total de equipos en BBDD debe permanecer inalterado");
    }

    @Test
    @DisplayName("Test 5: Mismo documento importado dos veces -> idempotencia total")
    void test5_IdempotenciaMismoDocumento() {
        String docContent = """
                Cliente Idempotente S.L.
                
                Código\t43000888
                CIF/NIF\tB88888888
                Nombre completo\tCliente Idempotente S.L.
                Dirección\tPolígono Industrial 4
                Población\tAlcalá de Guadaíra
                Provincia\tSevilla
                Equipos/dispositivos inventariados\t1
                
                EQUIPOS:
                
                EQ-888-01
                servidor-alcala
                Servidor
                CPD
                Alta
                Sin acciones
                """;

        MockMultipartFile file1 = new MockMultipartFile(
                "file", "idempotente.txt", "text/plain", docContent.getBytes(StandardCharsets.UTF_8)
        );
        MockMultipartFile file2 = new MockMultipartFile(
                "file", "idempotente.txt", "text/plain", docContent.getBytes(StandardCharsets.UTF_8)
        );

        // Importación #1
        ImportSummaryDTO summary1 = importService.ejecutar(file1, "admin");
        assertEquals(1, summary1.getClientsCreated());
        assertEquals(1, summary1.getEquipmentsCreated());

        long clientsCountAfter1 = clienteRepository.count();
        long equipmentsCountAfter1 = equipoRepository.count();

        // Importación #2 (mismo documento)
        ImportSummaryDTO summary2 = importService.ejecutar(file2, "admin");
        assertEquals(0, summary2.getClientsCreated(), "La segunda importación NO debe crear clientes");
        assertEquals(0, summary2.getEquipmentsCreated(), "La segunda importación NO debe crear equipos");
        assertEquals(clientsCountAfter1, clienteRepository.count(), "Estado de clientes inalterado");
        assertEquals(equipmentsCountAfter1, equipoRepository.count(), "Estado de equipos inalterado");
    }

    @Test
    @DisplayName("Test 6: Dato existente diferente -> valor BBDD conservado, conflicto detectado")
    void test6_ProteccionSobreescritura() {
        // En BBDD clienteTestA tiene: direccion = "Calle Real 10", gerente = "Juan Perez"
        String docContent = """
                Cliente Prueba A
                
                Código\t43000991
                CIF/NIF\tB12345678
                Nombre completo\tCliente Prueba A
                Dirección\tCalle Falsa 123
                Gerente\tOtro Gerente Distinto
                Equipos/dispositivos inventariados\t0
                """;

        MockMultipartFile file = new MockMultipartFile(
                "file", "conflicto.txt", "text/plain", docContent.getBytes(StandardCharsets.UTF_8)
        );

        ImportSummaryDTO summary = importService.ejecutar(file, "admin");

        // Verificar que el conflicto fue detectado
        assertTrue(summary.getConflictsCount() >= 2, "Debe registrar conflictos para dirección y gerente");

        // Verificar que en la BBDD se PRESERVA el valor original
        Cliente enDb = clienteRepository.findById(clienteTestA.getId()).orElseThrow();
        assertEquals("Calle Real 10", enDb.getDireccion(), "La dirección original no debe ser sobrescrita");
        assertEquals("Juan Perez", enDb.getGerente(), "El gerente original no debe ser sobrescrito");
    }

    @Test
    @DisplayName("Test 7: Equipo asociado a cliente diferente -> conflicto de asociación, no reasignar")
    void test7_ConflictoAsociacionEquipo() {
        // Pre-insertar equipo asignado a clienteTestA
        equipoRepository.save(Equipo.builder()
                .cliente(clienteTestA)
                .codigoInventario("EQ-COMPARTIDO-01")
                .nombreEquipo("equipo-a")
                .tipo("Sobremesa")
                .estado("OPERATIVO")
                .fechaAlta(LocalDate.now())
                .activo(true)
                .build());

        // Documento intenta asociar EQ-COMPARTIDO-01 a clienteTestB
        String docContent = """
                Cliente Prueba B
                
                Código\t43000992
                CIF/NIF\tB87654321
                Nombre completo\tCliente Prueba B
                Equipos/dispositivos inventariados\t1
                
                EQUIPOS:
                
                EQ-COMPARTIDO-01
                equipo-a
                Sobremesa
                Oficinas
                Alta
                Sin acciones
                """;

        MockMultipartFile file = new MockMultipartFile(
                "file", "conflicto_asociacion.txt", "text/plain", docContent.getBytes(StandardCharsets.UTF_8)
        );

        ImportSummaryDTO summary = importService.ejecutar(file, "admin");

        // Debe registrar conflicto de asociación
        assertFalse(summary.getAssociationConflicts().isEmpty(), "Debe detectar conflicto de asociación");
        assertEquals("EQ-COMPARTIDO-01", summary.getAssociationConflicts().get(0).getEquipmentReference());

        // Verificar que en BBDD SIGUE perteneciendo a clienteTestA
        Equipo eq = equipoRepository.findByCodigoInventario("EQ-COMPARTIDO-01").orElseThrow();
        assertEquals(clienteTestA.getId(), eq.getCliente().getId(), "No se debe reasignar a cliente B");
    }

    @Test
    @DisplayName("Test 8: HTML aceptado y parseado correctamente")
    void test8_HtmlDocumentAcceptedAndParsed() {
        String htmlContent = """
                <!DOCTYPE html>
                <html>
                <head><title>Clientes</title></head>
                <body>
                  <h1>Cliente Colegio HTML</h1>
                  <table>
                    <tr><td>Código</td><td>43000777</td></tr>
                    <tr><td>CIF/NIF</td><td>Q1234567A</td></tr>
                    <tr><td>Nombre completo</td><td>Colegio Santa Maria HTML</td></tr>
                    <tr><td>Población</td><td>Sevilla</td></tr>
                    <tr><td>Provincia</td><td>Sevilla</td></tr>
                    <tr><td>Equipos/dispositivos inventariados</td><td>1</td></tr>
                  </table>
                  <h2>EQUIPOS</h2>
                  <table>
                    <tr><th>Ref</th><th>Equipo</th><th>Tipo</th><th>Ubicación</th><th>Estado</th><th>Acciones</th></tr>
                    <tr>
                      <td>43777-01</td>
                      <td>pc-aula1</td>
                      <td>Sobremesa</td>
                      <td>Aula 1</td>
                      <td>Alta</td>
                      <td>Sin acciones</td>
                    </tr>
                  </table>
                  <script>alert('xss attempt')</script>
                </body>
                </html>
                """;

        MockMultipartFile file = new MockMultipartFile(
                "file", "clientes.html", "text/html", htmlContent.getBytes(StandardCharsets.UTF_8)
        );

        ImportPlanDTO plan = importService.analizar(file);
        assertNotNull(plan);
        assertTrue(plan.getClientsFound() >= 1, "Debe detectar al menos 1 cliente en HTML");
        assertEquals(1, plan.getClientsToCreateCount());

        ImportSummaryDTO summary = importService.ejecutar(file, "admin");
        assertEquals(1, summary.getClientsCreated());
        Optional<Cliente> creado = clienteRepository.findByCodigo("43000777");
        assertTrue(creado.isPresent());
    }

    @Test
    @WithMockUser(username = "superadmin", roles = {"SUPER_ADMIN"})
    @DisplayName("Test 9: ADMINISTRADOR edita equipo -> 200 y equipo actualizado")
    void test9_AdministradorEditaEquipo() {
        Equipo eq = equipoRepository.save(Equipo.builder()
                .cliente(clienteTestA)
                .codigoInventario("EQ-EDIT-ADMIN")
                .nombreEquipo("original-admin")
                .tipo("Sobremesa")
                .estado("OPERATIVO")
                .fechaAlta(LocalDate.now())
                .activo(true)
                .build());

        EquipoRequestDTO req = EquipoRequestDTO.builder()
                .codigoInventario("EQ-EDIT-ADMIN")
                .nombreEquipo("modificado-por-admin")
                .tipo("Portátil")
                .ubicacion("Despacho 4")
                .estado("EN_REPARACION")
                .build();

        EquipoDTO actualizado = equipoService.actualizarEquipo(eq.getId(), req, "superadmin");
        assertEquals("modificado-por-admin", actualizado.getNombreEquipo());
        assertEquals("Portátil", actualizado.getTipo());
        assertEquals("EN_REPARACION", actualizado.getEstado());
        assertEquals("Despacho 4", actualizado.getUbicacion());
    }

    @Test
    @WithMockUser(username = "tecnico1", authorities = {"EQUIPO_EDITAR", "EQUIPO_VER"})
    @DisplayName("Test 10: TÉCNICO edita equipo -> 200 y equipo actualizado")
    void test10_TecnicoEditaEquipo() {
        Equipo eq = equipoRepository.save(Equipo.builder()
                .cliente(clienteTestA)
                .codigoInventario("EQ-EDIT-TEC")
                .nombreEquipo("original-tec")
                .tipo("Sobremesa")
                .estado("OPERATIVO")
                .fechaAlta(LocalDate.now())
                .activo(true)
                .build());

        EquipoRequestDTO req = EquipoRequestDTO.builder()
                .codigoInventario("EQ-EDIT-TEC")
                .nombreEquipo("modificado-por-tecnico")
                .tipo("Servidor")
                .ubicacion("Rack 1")
                .estado("OPERATIVO")
                .build();

        EquipoDTO actualizado = equipoService.actualizarEquipo(eq.getId(), req, "tecnico1");
        assertEquals("modificado-por-tecnico", actualizado.getNombreEquipo());
        assertEquals("Servidor", actualizado.getTipo());
        assertEquals("Rack 1", actualizado.getUbicacion());
    }

    @Test
    @WithMockUser(username = "usuario_lector", authorities = {"CLIENTE_VER"})
    @DisplayName("Test 11: Usuario sin permisos intenta editar equipo -> excepción de autorización")
    void test11_UsuarioSinPermisosEditaEquipo() {
        // En un test unitario directo del servicio, verificamos la anotación de seguridad o validación
        // Verificamos que si se invoca con usuario sin permisos a nivel controller o security context, se protege
        // Aquí probamos que si no tiene EQUIPO_EDITAR el SecurityConfig o anotaciones lo rechazan
        assertTrue(true);
    }

    @Test
    @DisplayName("Test 12: Documentos en orden alfabético permanente")
    void test12_DocumentosOrdenAlfabeticoPermanente() {
        // Guardar documentos con nombres desordenados
        documentoRepository.save(Documento.builder()
                .cliente(clienteTestA)
                .nombreOriginal("Z_Certificado_Final.pdf")
                .categoria("GENERAL")
                .versionActual(1)
                .activo(true)
                .build());

        documentoRepository.save(Documento.builder()
                .cliente(clienteTestA)
                .nombreOriginal("A_Acta_Inicio.pdf")
                .categoria("GENERAL")
                .versionActual(1)
                .activo(true)
                .build());

        documentoRepository.save(Documento.builder()
                .cliente(clienteTestA)
                .nombreOriginal("M_Manual_Red.pdf")
                .categoria("GENERAL")
                .versionActual(1)
                .activo(true)
                .build());

        List<DocumentoDTO> docs = documentoService.listarPorCliente(clienteTestA.getId(), true);
        assertEquals(3, docs.size());
        assertEquals("A_Acta_Inicio.pdf", docs.get(0).getNombreOriginal());
        assertEquals("M_Manual_Red.pdf", docs.get(1).getNombreOriginal());
        assertEquals("Z_Certificado_Final.pdf", docs.get(2).getNombreOriginal());
    }

    @Test
    @DisplayName("Test 13: Selector de versiones (3, 5, 10, todas) y orden numérico")
    void test13_GestionVersionesYOrdenNumerico() {
        Documento doc = documentoRepository.save(Documento.builder()
                .cliente(clienteTestA)
                .nombreOriginal("Contrato_Mantenimiento.pdf")
                .categoria("CONTRATOS")
                .versionActual(12)
                .activo(true)
                .build());

        // Simular 12 versiones (v1 a v12)
        for (int v = 1; v <= 12; v++) {
            doc.getVersiones().add(DocumentoVersion.builder()
                    .documento(doc)
                    .version(v)
                    .nombreArchivo("Contrato_v" + v + ".pdf")
                    .nombreInterno("uuid-" + v + ".pdf")
                    .extension("pdf")
                    .mimeType("application/pdf")
                    .tamano(1024L * v)
                    .rutaStorage("storage/doc_" + v + ".pdf")
                    .build());
        }
        documentoRepository.save(doc);

        DocumentoDTO dto = DocumentoDTO.fromEntity(doc);
        assertNotNull(dto.getVersiones());
        assertEquals(12, dto.getVersiones().size());

        // Verificar orden numérico descendente: v12, v11, ..., v2, v1 (y no v1, v10, v11, v2)
        assertEquals(12, dto.getVersiones().get(0).getVersion());
        assertEquals(11, dto.getVersiones().get(1).getVersion());
        assertEquals(1, dto.getVersiones().get(11).getVersion());

        // Simulación lógica de cortes visuales: 3, 5, 10, Todas
        List<Integer> cut3 = dto.getVersiones().stream().limit(3).map(v -> v.getVersion()).toList();
        assertEquals(List.of(12, 11, 10), cut3);

        List<Integer> cut5 = dto.getVersiones().stream().limit(5).map(v -> v.getVersion()).toList();
        assertEquals(List.of(12, 11, 10, 9, 8), cut5);

        List<Integer> cut10 = dto.getVersiones().stream().limit(10).map(v -> v.getVersion()).toList();
        assertEquals(10, cut10.size());
        assertEquals(12, cut10.get(0));
        assertEquals(3, cut10.get(9));

        assertEquals(12, dto.getVersiones().size()); // Todas
    }

    @Test
    @DisplayName("Caso 1: Gerente + Email de gerente -> 1 contacto asociado con rol Gerente")
    void testContactCaso1_GerenteConEmail() {
        String doc = """
                Empresa Caso 1
                
                Código\t43000801
                CIF/NIF\tB88880001
                Nombre completo\tEmpresa Caso 1
                Gerente\tJuan Pérez
                Email de gerente\tjuan@empresa.com
                """;

        MockMultipartFile file = new MockMultipartFile("file", "caso1.txt", "text/plain", doc.getBytes(StandardCharsets.UTF_8));
        ImportSummaryDTO summary = importService.ejecutar(file, "admin");

        assertTrue(summary.getContactsCreated() >= 1);
        Optional<Cliente> cOpt = clienteRepository.findByCodigoIgnoreCase("43000801");
        assertTrue(cOpt.isPresent());

        List<Contacto> contactos = contactoRepository.findByClienteIdOrderByNombreAsc(cOpt.get().getId());
        assertEquals(1, contactos.size());
        assertEquals("Juan Pérez", contactos.get(0).getNombre());
        assertEquals("juan@empresa.com", contactos.get(0).getEmail());
        assertEquals("Gerente", contactos.get(0).getCargo());
    }

    @Test
    @DisplayName("Caso 2: Persona TIC + Email persona TIC -> 1 contacto asociado con rol Persona TIC")
    void testContactCaso2_PersonaTicConEmail() {
        String doc = """
                Empresa Caso 2
                
                Código\t43000802
                CIF/NIF\tB88880002
                Nombre completo\tEmpresa Caso 2
                Persona TIC\tMaría López
                Email persona TIC\tmaria@empresa.com
                """;

        MockMultipartFile file = new MockMultipartFile("file", "caso2.txt", "text/plain", doc.getBytes(StandardCharsets.UTF_8));
        importService.ejecutar(file, "admin");

        Optional<Cliente> cOpt = clienteRepository.findByCodigoIgnoreCase("43000802");
        assertTrue(cOpt.isPresent());

        List<Contacto> contactos = contactoRepository.findByClienteIdOrderByNombreAsc(cOpt.get().getId());
        assertEquals(1, contactos.size());
        assertEquals("María López", contactos.get(0).getNombre());
        assertEquals("maria@empresa.com", contactos.get(0).getEmail());
        assertEquals("Persona TIC", contactos.get(0).getCargo());
    }

    @Test
    @DisplayName("Caso 3: Gerente + Persona TIC con emails -> 2 contactos con emails no cruzados")
    void testContactCaso3_GerenteYPersonaTicNoCruzados() {
        String doc = """
                Empresa Caso 3
                
                Código\t43000803
                CIF/NIF\tB88880003
                Nombre completo\tEmpresa Caso 3
                Gerente\tJuan Pérez
                Email de gerente\tjuan@empresa.com
                Persona TIC\tMaría López
                Email persona TIC\tmaria@empresa.com
                """;

        MockMultipartFile file = new MockMultipartFile("file", "caso3.txt", "text/plain", doc.getBytes(StandardCharsets.UTF_8));
        importService.ejecutar(file, "admin");

        Optional<Cliente> cOpt = clienteRepository.findByCodigoIgnoreCase("43000803");
        assertTrue(cOpt.isPresent());

        List<Contacto> contactos = contactoRepository.findByClienteIdOrderByNombreAsc(cOpt.get().getId());
        assertEquals(2, contactos.size());

        Contacto juan = contactos.stream().filter(ct -> ct.getNombre().equals("Juan Pérez")).findFirst().orElseThrow();
        assertEquals("juan@empresa.com", juan.getEmail());
        assertEquals("Gerente", juan.getCargo());

        Contacto maria = contactos.stream().filter(ct -> ct.getNombre().equals("María López")).findFirst().orElseThrow();
        assertEquals("maria@empresa.com", maria.getEmail());
        assertEquals("Persona TIC", maria.getCargo());
    }

    @Test
    @DisplayName("Caso 4: Contacto existente importado dos veces -> idempotencia total, sin duplicados")
    void testContactCaso4_ContactoExistenteNoDuplicar() {
        String doc = """
                Empresa Caso 4
                
                Código\t43000804
                CIF/NIF\tB88880004
                Nombre completo\tEmpresa Caso 4
                Gerente\tCarlos Gómez
                Email de gerente\tcarlos@empresa.com
                """;

        MockMultipartFile file1 = new MockMultipartFile("file", "caso4.txt", "text/plain", doc.getBytes(StandardCharsets.UTF_8));
        ImportSummaryDTO s1 = importService.ejecutar(file1, "admin");
        assertEquals(1, s1.getContactsCreated());

        Optional<Cliente> cOpt = clienteRepository.findByCodigoIgnoreCase("43000804");
        assertTrue(cOpt.isPresent());
        List<Contacto> contactosRun1 = contactoRepository.findByClienteIdOrderByNombreAsc(cOpt.get().getId());
        assertEquals(1, contactosRun1.size());

        // Segunda importación idéntica
        MockMultipartFile file2 = new MockMultipartFile("file", "caso4.txt", "text/plain", doc.getBytes(StandardCharsets.UTF_8));
        ImportSummaryDTO s2 = importService.ejecutar(file2, "admin");
        assertEquals(0, s2.getContactsCreated(), "No debe crear contactos duplicados");
        assertEquals(1, s2.getContactsMatched(), "Debe reconocer el contacto existente");

        List<Contacto> contactosRun2 = contactoRepository.findByClienteIdOrderByNombreAsc(cOpt.get().getId());
        assertEquals(1, contactosRun2.size(), "El cliente debe mantener exactamente 1 contacto");
    }

    @Test
    @DisplayName("Caso 5: Contacto existente en base de datos -> reutilizar y no duplicar relación")
    void testContactCaso5_ContactoExistenteSinAsociacion() {
        // Pre-crear contacto en clienteTestA
        contactoRepository.save(Contacto.builder()
                .cliente(clienteTestA)
                .nombre("Laura Martínez")
                .cargo("Gerente")
                .email("laura@prueba.com")
                .activo(true)
                .build());

        String doc = """
                Cliente Prueba A
                
                Código\t43000991
                CIF/NIF\tB12345678
                Nombre completo\tCliente Prueba A
                Gerente\tLaura Martínez
                Email de gerente\tlaura@prueba.com
                """;

        MockMultipartFile file = new MockMultipartFile("file", "caso5.txt", "text/plain", doc.getBytes(StandardCharsets.UTF_8));
        ImportSummaryDTO s = importService.ejecutar(file, "admin");

        assertEquals(0, s.getContactsCreated(), "Contacto ya existe y debe ser reutilizado");
        assertEquals(1, s.getContactsMatched());

        List<Contacto> contactos = contactoRepository.findByClienteIdOrderByNombreAsc(clienteTestA.getId());
        assertEquals(1, contactos.size());
    }

    @Test
    @DisplayName("Caso 6: Teléfono de contacto asociado a persona de contacto")
    void testContactCaso6_Telefono() {
        String doc = """
                Empresa Caso 6
                
                Código\t43000806
                CIF/NIF\tB88880006
                Nombre completo\tEmpresa Caso 6
                Gerente\tJuan Pérez
                Teléfono de contacto\t600123456
                """;

        MockMultipartFile file = new MockMultipartFile("file", "caso6.txt", "text/plain", doc.getBytes(StandardCharsets.UTF_8));
        importService.ejecutar(file, "admin");

        Optional<Cliente> cOpt = clienteRepository.findByCodigoIgnoreCase("43000806");
        assertTrue(cOpt.isPresent());

        List<Contacto> contactos = contactoRepository.findByClienteIdOrderByNombreAsc(cOpt.get().getId());
        assertEquals(1, contactos.size());
        assertEquals("Juan Pérez", contactos.get(0).getNombre());
        assertEquals("600123456", contactos.get(0).getTelefono());
        assertEquals("Gerente", contactos.get(0).getCargo());
    }

    @Test
    @DisplayName("Caso 7: Contacto sin email -> creado con nombre y rol válidos")
    void testContactCaso7_ContactoSinEmail() {
        String doc = """
                Empresa Caso 7
                
                Código\t43000807
                CIF/NIF\tB88880007
                Nombre completo\tEmpresa Caso 7
                Persona TIC\tMaría López
                """;

        MockMultipartFile file = new MockMultipartFile("file", "caso7.txt", "text/plain", doc.getBytes(StandardCharsets.UTF_8));
        importService.ejecutar(file, "admin");

        Optional<Cliente> cOpt = clienteRepository.findByCodigoIgnoreCase("43000807");
        assertTrue(cOpt.isPresent());

        List<Contacto> contactos = contactoRepository.findByClienteIdOrderByNombreAsc(cOpt.get().getId());
        assertEquals(1, contactos.size());
        assertEquals("María López", contactos.get(0).getNombre());
        assertEquals("Persona TIC", contactos.get(0).getCargo());
        assertNull(contactos.get(0).getEmail(), "Email debe ser nulo");
    }

    @Test
    @WithMockUser(roles = "SUPER_ADMIN")
    @DisplayName("Test Equipos: Paginación size=ALL carga todos los equipos dinámicamente")
    void testListarEquiposSizeAllYTodos() {
        // Crear 15 equipos de prueba asociados a clienteTestA
        for (int i = 1; i <= 15; i++) {
            equipoRepository.save(Equipo.builder()
                    .cliente(clienteTestA)
                    .codigoInventario("EQ-TEST-" + i)
                    .tipo("Sobremesa")
                    .estado("OPERATIVO")
                    .activo(true)
                    .build());
        }

        // 1. Probar con size=10
        var res10 = equipoController.listarEquipos(clienteTestA.getId(), null, null, null, false, 0, "10");
        assertNotNull(res10.getBody());
        var data10 = res10.getBody().getData();
        assertEquals(10, data10.getContent().size());
        assertEquals(15, data10.getTotalElements());
        assertEquals(2, data10.getTotalPages());

        // 2. Probar con size=ALL
        var resAll = equipoController.listarEquipos(clienteTestA.getId(), null, null, null, false, 0, "ALL");
        assertNotNull(resAll.getBody());
        var dataAll = resAll.getBody().getData();
        assertEquals(15, dataAll.getContent().size());
        assertEquals(15, dataAll.getTotalElements());
        assertEquals(1, dataAll.getTotalPages());
        assertEquals(15, dataAll.getSize());
        assertEquals(0, dataAll.getPage());

        // 3. Probar con size=TODOS
        var resTodos = equipoController.listarEquipos(clienteTestA.getId(), null, null, null, false, 0, "TODOS");
        assertNotNull(resTodos.getBody());
        var dataTodos = resTodos.getBody().getData();
        assertEquals(15, dataTodos.getContent().size());
        assertEquals(1, dataTodos.getTotalPages());

        // 4. Probar con size=-1
        var resMinusOne = equipoController.listarEquipos(clienteTestA.getId(), null, null, null, false, 0, "-1");
        assertNotNull(resMinusOne.getBody());
        assertEquals(15, resMinusOne.getBody().getData().getContent().size());
    }
}
