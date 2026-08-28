package com.abaxial.portal.audit.service;

import com.abaxial.portal.audit.dto.AuditoriaDTO;
import com.abaxial.portal.audit.entity.Auditoria;
import com.abaxial.portal.audit.repository.AuditoriaRepository;
import com.abaxial.portal.common.dto.PaginatedResponse;
import com.abaxial.portal.users.entity.Usuario;
import com.abaxial.portal.users.repository.UsuarioRepository;
import jakarta.persistence.criteria.Predicate;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuditoriaService {

    private static final Logger log = LoggerFactory.getLogger(AuditoriaService.class);

    private final AuditoriaRepository auditoriaRepository;
    private final UsuarioRepository usuarioRepository;

    @Async
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void registrarAsync(String username, String accion, String entidad, Long entidadId, String detalles) {
        registrarSync(username, accion, entidad, entidadId, detalles);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void registrarSync(String username, String accion, String entidad, Long entidadId, String detalles) {
        try {
            Usuario usuario = null;
            if (username != null && !username.isBlank()) {
                usuario = usuarioRepository.findByUsername(username).orElse(null);
            }

            String ip = null;
            String userAgent = null;

            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes != null) {
                HttpServletRequest request = attributes.getRequest();
                ip = extractClientIp(request);
                userAgent = request.getHeader("User-Agent");
                if (userAgent != null && userAgent.length() > 250) {
                    userAgent = userAgent.substring(0, 250);
                }
            }

            Auditoria auditoria = Auditoria.builder()
                    .usuario(usuario)
                    .username(username != null ? username : (usuario != null ? usuario.getUsername() : "SISTEMA"))
                    .accion(accion)
                    .entidad(entidad)
                    .entidadId(entidadId)
                    .fecha(LocalDateTime.now())
                    .ip(ip)
                    .userAgent(userAgent)
                    .detalles(detalles)
                    .build();

            auditoriaRepository.save(auditoria);
        } catch (Exception e) {
            log.error("Error al registrar auditoría [accion={}, entidad={}]: {}", accion, entidad, e.getMessage());
        }
    }

    @Transactional(readOnly = true)
    public PaginatedResponse<AuditoriaDTO> listarAuditorias(
            Long usuarioId, String search, String accion, String entidad,
            LocalDateTime desde, LocalDateTime hasta, Pageable pageable) {

        Specification<Auditoria> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (usuarioId != null) {
                predicates.add(cb.equal(root.get("usuario").get("id"), usuarioId));
            }
            if (accion != null && !accion.isBlank()) {
                predicates.add(cb.equal(root.get("accion"), accion));
            }
            if (entidad != null && !entidad.isBlank()) {
                predicates.add(cb.equal(root.get("entidad"), entidad));
            }
            if (desde != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("fecha"), desde));
            }
            if (hasta != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("fecha"), hasta));
            }
            if (search != null && !search.isBlank()) {
                String pattern = "%" + search.trim().toLowerCase() + "%";
                predicates.add(cb.or(
                        cb.like(cb.lower(root.get("username")), pattern),
                        cb.like(cb.lower(root.get("accion")), pattern),
                        cb.like(cb.lower(root.get("entidad")), pattern),
                        cb.like(cb.lower(root.get("detalles")), pattern)
                ));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        Page<Auditoria> page = auditoriaRepository.findAll(spec, pageable);
        return PaginatedResponse.from(page.map(AuditoriaDTO::fromEntity));
    }

    @Transactional(readOnly = true)
    public List<AuditoriaDTO> obtenerActividadReciente() {
        return auditoriaRepository.findTop10ByOrderByFechaDesc().stream()
                .map(AuditoriaDTO::fromEntity)
                .collect(Collectors.toList());
    }

    private String extractClientIp(HttpServletRequest request) {
        String xfHeader = request.getHeader("X-Forwarded-For");
        if (xfHeader == null || xfHeader.isEmpty() || "unknown".equalsIgnoreCase(xfHeader)) {
            return request.getRemoteAddr();
        }
        return xfHeader.split(",")[0].trim();
    }
}
