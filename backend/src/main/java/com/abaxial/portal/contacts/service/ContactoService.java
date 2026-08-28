package com.abaxial.portal.contacts.service;

import com.abaxial.portal.audit.service.AuditoriaService;
import com.abaxial.portal.clients.entity.Cliente;
import com.abaxial.portal.clients.repository.ClienteRepository;
import com.abaxial.portal.common.exception.ResourceNotFoundException;
import com.abaxial.portal.contacts.dto.ContactoDTO;
import com.abaxial.portal.contacts.dto.ContactoRequestDTO;
import com.abaxial.portal.contacts.entity.Contacto;
import com.abaxial.portal.contacts.repository.ContactoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ContactoService {

    private final ContactoRepository contactoRepository;
    private final ClienteRepository clienteRepository;
    private final AuditoriaService auditoriaService;

    @Transactional(readOnly = true)
    public List<ContactoDTO> listarPorCliente(Long clienteId, boolean soloActivos) {
        List<Contacto> list = soloActivos
                ? contactoRepository.findByClienteIdAndActivoTrueOrderByNombreAsc(clienteId)
                : contactoRepository.findByClienteIdOrderByNombreAsc(clienteId);
        return list.stream().map(ContactoDTO::fromEntity).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ContactoDTO obtenerPorId(Long id) {
        Contacto c = contactoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Contacto no encontrado con ID: " + id));
        return ContactoDTO.fromEntity(c);
    }

    @Transactional
    public ContactoDTO crearContacto(Long clienteId, ContactoRequestDTO req, String currentUsername) {
        Cliente cliente = clienteRepository.findById(clienteId)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado con ID: " + clienteId));

        Contacto contacto = Contacto.builder()
                .cliente(cliente)
                .nombre(req.getNombre().trim())
                .apellidos(req.getApellidos())
                .cargo(req.getCargo())
                .email(req.getEmail())
                .telefono(req.getTelefono())
                .telefonoFijo(req.getTelefonoFijo())
                .observaciones(req.getObservaciones())
                .activo(req.getActivo() != null ? req.getActivo() : true)
                .fechaCreacion(LocalDateTime.now())
                .fechaModificacion(LocalDateTime.now())
                .build();

        Contacto guardado = contactoRepository.save(contacto);

        auditoriaService.registrarAsync(
                currentUsername,
                "CONTACTO_CREADO",
                "Contacto",
                guardado.getId(),
                "Contacto creado: " + guardado.getNombre() + " para Cliente " + cliente.getNombre()
        );

        return ContactoDTO.fromEntity(guardado);
    }

    @Transactional
    public ContactoDTO actualizarContacto(Long id, ContactoRequestDTO req, String currentUsername) {
        Contacto contacto = contactoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Contacto no encontrado con ID: " + id));

        if (req.getNombre() != null && !req.getNombre().isBlank()) {
            contacto.setNombre(req.getNombre().trim());
        }
        if (req.getApellidos() != null) {
            contacto.setApellidos(req.getApellidos());
        }
        if (req.getCargo() != null) {
            contacto.setCargo(req.getCargo());
        }
        if (req.getEmail() != null) {
            contacto.setEmail(req.getEmail());
        }
        if (req.getTelefono() != null) {
            contacto.setTelefono(req.getTelefono());
        }
        if (req.getTelefonoFijo() != null) {
            contacto.setTelefonoFijo(req.getTelefonoFijo());
        }
        if (req.getObservaciones() != null) {
            contacto.setObservaciones(req.getObservaciones());
        }
        if (req.getActivo() != null) {
            contacto.setActivo(req.getActivo());
        }

        contacto.setFechaModificacion(LocalDateTime.now());
        Contacto actualizado = contactoRepository.save(contacto);

        auditoriaService.registrarAsync(
                currentUsername,
                "CONTACTO_MODIFICADO",
                "Contacto",
                actualizado.getId(),
                "Contacto modificado: " + actualizado.getNombre()
        );

        return ContactoDTO.fromEntity(actualizado);
    }

    @Transactional
    public void desactivarContacto(Long id, String currentUsername) {
        Contacto contacto = contactoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Contacto no encontrado con ID: " + id));

        contacto.setActivo(false);
        contacto.setFechaModificacion(LocalDateTime.now());
        contactoRepository.save(contacto);

        auditoriaService.registrarAsync(
                currentUsername,
                "CONTACTO_DESACTIVADO",
                "Contacto",
                contacto.getId(),
                "Contacto desactivado: " + contacto.getNombre()
        );
    }
}
