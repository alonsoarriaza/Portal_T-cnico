package com.abaxial.portal;

import com.abaxial.portal.clients.dto.ClienteDetailDTO;
import com.abaxial.portal.clients.dto.ClienteRequestDTO;
import com.abaxial.portal.clients.service.ClienteService;
import com.abaxial.portal.common.exception.ConflictException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class ClienteServiceTest {

    @Autowired
    private ClienteService clienteService;

    @Test
    void testCrearYConsultarCliente() {
        ClienteRequestDTO request = ClienteRequestDTO.builder()
                .nifCif("B99887766")
                .nombre("Empresa Prueba S.L.")
                .estado("ALTA")
                .mantenimiento("INTEGRAL")
                .poblacion("Madrid")
                .provincia("Madrid")
                .gerente("Carlos Gómez")
                .build();

        ClienteDetailDTO creado = clienteService.crearCliente(request, "admin");

        assertNotNull(creado.getId());
        assertEquals("B99887766", creado.getNifCif());
        assertEquals("Empresa Prueba S.L.", creado.getNombre());
        assertTrue(creado.isActivo());
        assertNotNull(creado.getCodigo());

        ClienteDetailDTO obtenido = clienteService.obtenerDetalleCliente(creado.getId());
        assertEquals(creado.getId(), obtenido.getId());
        assertEquals("INTEGRAL", obtenido.getMantenimiento());
    }

    @Test
    void testPrevenirNifDuplicado() {
        ClienteRequestDTO req1 = ClienteRequestDTO.builder()
                .nifCif("A11112222")
                .nombre("Empresa Uno")
                .build();

        clienteService.crearCliente(req1, "admin");

        ClienteRequestDTO req2 = ClienteRequestDTO.builder()
                .nifCif("A11112222")
                .nombre("Empresa Dos")
                .build();

        assertThrows(ConflictException.class, () -> clienteService.crearCliente(req2, "admin"));
    }

    @Test
    void testSoftDeleteCliente() {
        ClienteRequestDTO req = ClienteRequestDTO.builder()
                .nifCif("B33334444")
                .nombre("Empresa A Desactivar")
                .build();

        ClienteDetailDTO creado = clienteService.crearCliente(req, "admin");
        assertTrue(creado.isActivo());

        clienteService.desactivarCliente(creado.getId(), "admin");

        ClienteDetailDTO actualizado = clienteService.obtenerDetalleCliente(creado.getId());
        assertFalse(actualizado.isActivo());
        assertEquals("BAJA", actualizado.getEstado());
    }
}
