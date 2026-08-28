package com.abaxial.portal.websites.service;

import com.abaxial.portal.audit.service.AuditoriaService;
import com.abaxial.portal.clients.entity.Cliente;
import com.abaxial.portal.clients.repository.ClienteRepository;
import com.abaxial.portal.common.dto.PaginatedResponse;
import com.abaxial.portal.common.exception.ResourceNotFoundException;
import com.abaxial.portal.websites.dto.WebDTO;
import com.abaxial.portal.websites.dto.WebRequestDTO;
import com.abaxial.portal.websites.entity.Web;
import com.abaxial.portal.websites.repository.WebRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WebService {

    private final WebRepository webRepository;
    private final ClienteRepository clienteRepository;
    private final AuditoriaService auditoriaService;

    @Transactional(readOnly = true)
    public PaginatedResponse<WebDTO> listarWebs(
            Long clienteId, String estado, String search,
            boolean includeInactive, Pageable pageable
    ) {
        Page<Web> page = webRepository.findAllFiltered(clienteId, estado, search, includeInactive, pageable);
        return PaginatedResponse.from(page.map(WebDTO::fromEntity));
    }

    @Transactional(readOnly = true)
    public List<WebDTO> listarPorCliente(Long clienteId, boolean soloActivos) {
        List<Web> list = soloActivos
                ? webRepository.findByClienteIdAndActivoTrueOrderByNombreAsc(clienteId)
                : webRepository.findByClienteIdOrderByNombreAsc(clienteId);
        return list.stream().map(WebDTO::fromEntity).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public WebDTO obtenerPorId(Long id) {
        Web w = webRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Web no encontrada con ID: " + id));
        return WebDTO.fromEntity(w);
    }

    @Transactional
    public WebDTO crearWeb(Long clienteId, WebRequestDTO req, String currentUsername) {
        Cliente cliente = clienteRepository.findById(clienteId)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado con ID: " + clienteId));

        String url = req.getUrl().trim();
        if (!url.startsWith("http://") && !url.startsWith("https://")) {
            url = "https://" + url;
        }

        Web web = Web.builder()
                .cliente(cliente)
                .nombre(req.getNombre().trim())
                .url(url)
                .estado(req.getEstado() != null && !req.getEstado().isBlank() ? req.getEstado().toUpperCase() : "ONLINE")
                .descripcion(req.getDescripcion())
                .fechaRegistro(req.getFechaRegistro() != null ? req.getFechaRegistro() : LocalDate.now())
                .observaciones(req.getObservaciones())
                .activo(req.getActivo() != null ? req.getActivo() : true)
                .fechaCreacion(LocalDateTime.now())
                .fechaModificacion(LocalDateTime.now())
                .build();

        Web guardado = webRepository.save(web);

        auditoriaService.registrarAsync(
                currentUsername,
                "WEB_CREADA",
                "Web",
                guardado.getId(),
                "Web registrada: " + guardado.getNombre() + " (" + guardado.getUrl() + ") para Cliente " + cliente.getNombre()
        );

        return WebDTO.fromEntity(guardado);
    }

    @Transactional
    public WebDTO actualizarWeb(Long id, WebRequestDTO req, String currentUsername) {
        Web web = webRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Web no encontrada con ID: " + id));

        if (req.getNombre() != null && !req.getNombre().isBlank()) {
            web.setNombre(req.getNombre().trim());
        }
        if (req.getUrl() != null && !req.getUrl().isBlank()) {
            String url = req.getUrl().trim();
            if (!url.startsWith("http://") && !url.startsWith("https://")) {
                url = "https://" + url;
            }
            web.setUrl(url);
        }
        if (req.getEstado() != null && !req.getEstado().isBlank()) {
            web.setEstado(req.getEstado().toUpperCase());
        }
        if (req.getDescripcion() != null) {
            web.setDescripcion(req.getDescripcion());
        }
        if (req.getFechaRegistro() != null) {
            web.setFechaRegistro(req.getFechaRegistro());
        }
        if (req.getObservaciones() != null) {
            web.setObservaciones(req.getObservaciones());
        }
        if (req.getActivo() != null) {
            web.setActivo(req.getActivo());
        }

        web.setFechaModificacion(LocalDateTime.now());
        Web actualizado = webRepository.save(web);

        auditoriaService.registrarAsync(
                currentUsername,
                "WEB_MODIFICADA",
                "Web",
                actualizado.getId(),
                "Web modificada: " + actualizado.getNombre()
        );

        return WebDTO.fromEntity(actualizado);
    }

    @Transactional
    public void desactivarWeb(Long id, String currentUsername) {
        Web web = webRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Web no encontrada con ID: " + id));

        web.setActivo(false);
        web.setEstado("OFFLINE");
        web.setFechaModificacion(LocalDateTime.now());
        webRepository.save(web);

        auditoriaService.registrarAsync(
                currentUsername,
                "WEB_DESACTIVADA",
                "Web",
                web.getId(),
                "Web desactivada: " + web.getNombre()
        );
    }
}
