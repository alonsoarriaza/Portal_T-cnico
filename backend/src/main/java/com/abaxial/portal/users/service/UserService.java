package com.abaxial.portal.users.service;

import com.abaxial.portal.audit.service.AuditoriaService;
import com.abaxial.portal.common.dto.PaginatedResponse;
import com.abaxial.portal.common.exception.BadRequestException;
import com.abaxial.portal.common.exception.ConflictException;
import com.abaxial.portal.common.exception.ResourceNotFoundException;
import com.abaxial.portal.users.dto.*;
import com.abaxial.portal.users.entity.Permiso;
import com.abaxial.portal.users.entity.Rol;
import com.abaxial.portal.users.entity.Usuario;
import com.abaxial.portal.users.repository.PermisoRepository;
import com.abaxial.portal.users.repository.RolRepository;
import com.abaxial.portal.users.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final PermisoRepository permisoRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuditoriaService auditoriaService;

    @Transactional(readOnly = true)
    public PaginatedResponse<UserResponseDTO> listarUsuarios(String search, boolean includeInactive, Pageable pageable) {
        Page<Usuario> page = usuarioRepository.findAllFiltered(search, includeInactive, pageable);
        return PaginatedResponse.from(page.map(UserResponseDTO::fromEntity));
    }

    @Transactional(readOnly = true)
    public UserResponseDTO obtenerPorId(Long id) {
        Usuario u = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con ID: " + id));
        return UserResponseDTO.fromEntity(u);
    }

    @Transactional
    public UserResponseDTO crearUsuario(UserCreateRequest req, String currentUsername) {
        String username = req.getUsername().trim();
        String email = req.getEmail().trim().toLowerCase();

        if (usuarioRepository.existsByUsername(username)) {
            throw new ConflictException("Ya existe un usuario con el nombre de usuario: " + username);
        }
        if (usuarioRepository.existsByEmail(email)) {
            throw new ConflictException("Ya existe un usuario con el email: " + email);
        }

        Set<Rol> roles = resolveRoles(req.getRoles());

        Usuario usuario = Usuario.builder()
                .username(username)
                .email(email)
                .passwordHash(passwordEncoder.encode(req.getPassword()))
                .nombre(req.getNombre().trim())
                .apellidos(req.getApellidos() != null ? req.getApellidos().trim() : "")
                .activo(true)
                .fechaAlta(LocalDateTime.now())
                .fechaModificacion(LocalDateTime.now())
                .roles(roles)
                .build();

        Usuario guardado = usuarioRepository.save(usuario);

        String rolesStr = roles.stream().map(Rol::getCodigo).collect(Collectors.joining(", "));
        auditoriaService.registrarAsync(
                currentUsername,
                "USUARIO_CREADO",
                "Usuario",
                guardado.getId(),
                "Usuario creado: " + guardado.getUsername() + " (" + guardado.getEmail() + ") con roles [" + rolesStr + "]"
        );

        return UserResponseDTO.fromEntity(guardado);
    }

    @Transactional
    public UserResponseDTO actualizarUsuario(Long id, UserUpdateRequest req, String currentUsername) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con ID: " + id));

        List<String> cambios = new ArrayList<>();

        if (req.getEmail() != null && !req.getEmail().isBlank()) {
            String nuevoEmail = req.getEmail().trim().toLowerCase();
            if (!nuevoEmail.equalsIgnoreCase(usuario.getEmail())) {
                if (usuarioRepository.existsByEmail(nuevoEmail)) {
                    throw new ConflictException("Ya existe otro usuario con el email: " + nuevoEmail);
                }
                cambios.add("Email: antes '" + usuario.getEmail() + "' -> después '" + nuevoEmail + "'");
                usuario.setEmail(nuevoEmail);
            }
        }

        if (req.getNombre() != null && !req.getNombre().isBlank()) {
            String nuevoNombre = req.getNombre().trim();
            if (!nuevoNombre.equals(usuario.getNombre())) {
                cambios.add("Nombre: antes '" + usuario.getNombre() + "' -> después '" + nuevoNombre + "'");
                usuario.setNombre(nuevoNombre);
            }
        }

        if (req.getApellidos() != null && !Objects.equals(req.getApellidos().trim(), usuario.getApellidos())) {
            cambios.add("Apellidos: antes '" + usuario.getApellidos() + "' -> después '" + req.getApellidos().trim() + "'");
            usuario.setApellidos(req.getApellidos().trim());
        }

        if (req.getPassword() != null && !req.getPassword().isBlank()) {
            if (req.getPassword().length() < 4) {
                throw new BadRequestException("La contraseña debe tener al menos 4 caracteres");
            }
            usuario.setPasswordHash(passwordEncoder.encode(req.getPassword()));
            cambios.add("Contraseña actualizada");
        }

        if (req.getActivo() != null && !Objects.equals(req.getActivo(), usuario.getActivo())) {
            cambios.add("Activo: antes '" + usuario.getActivo() + "' -> después '" + req.getActivo() + "'");
            usuario.setActivo(req.getActivo());
            if (Boolean.FALSE.equals(req.getActivo())) {
                usuario.setFechaEliminacion(LocalDateTime.now());
                usuarioRepository.findByUsername(currentUsername).ifPresent(u -> usuario.setEliminadoPor(u.getId()));
            } else {
                usuario.setFechaEliminacion(null);
                usuario.setEliminadoPor(null);
            }
        }

        if (req.getRoles() != null && !req.getRoles().isEmpty()) {
            Set<Rol> nuevosRoles = resolveRoles(req.getRoles());
            String antiguosRolesStr = usuario.getRoles().stream().map(Rol::getCodigo).collect(Collectors.joining(", "));
            String nuevosRolesStr = nuevosRoles.stream().map(Rol::getCodigo).collect(Collectors.joining(", "));
            if (!antiguosRolesStr.equals(nuevosRolesStr)) {
                cambios.add("Roles: antes [" + antiguosRolesStr + "] -> después [" + nuevosRolesStr + "]");
                usuario.setRoles(nuevosRoles);
            }
        }

        usuario.setFechaModificacion(LocalDateTime.now());
        Usuario actualizado = usuarioRepository.save(usuario);

        String detalleAuditoria = cambios.isEmpty()
                ? "Usuario modificado sin cambios significativos: " + actualizado.getUsername()
                : "Modificó usuario " + actualizado.getUsername() + ". Cambios: " + String.join(" | ", cambios);

        auditoriaService.registrarAsync(
                currentUsername,
                "USUARIO_MODIFICADO",
                "Usuario",
                actualizado.getId(),
                detalleAuditoria
        );

        return UserResponseDTO.fromEntity(actualizado);
    }

    @Transactional
    public void desactivarUsuario(Long id, String currentUsername) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con ID: " + id));

        if ("admin".equalsIgnoreCase(usuario.getUsername())) {
            throw new BadRequestException("No es posible desactivar al usuario administrador principal");
        }

        usuario.setActivo(false);
        usuario.setFechaEliminacion(LocalDateTime.now());
        usuarioRepository.findByUsername(currentUsername).ifPresent(u -> usuario.setEliminadoPor(u.getId()));
        usuarioRepository.save(usuario);

        auditoriaService.registrarAsync(
                currentUsername,
                "USUARIO_DESACTIVADO",
                "Usuario",
                usuario.getId(),
                "Usuario desactivado (soft delete): " + usuario.getUsername()
        );
    }

    @Transactional(readOnly = true)
    public List<RoleDTO> listarRoles() {
        return rolRepository.findAll().stream().map(RoleDTO::fromEntity).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<PermisoDTO> listarPermisos() {
        return permisoRepository.findAll().stream().map(PermisoDTO::fromEntity).collect(Collectors.toList());
    }

    private Set<Rol> resolveRoles(Set<String> roleCodes) {
        Set<Rol> roles = new HashSet<>();
        if (roleCodes == null || roleCodes.isEmpty()) {
            rolRepository.findByCodigo("TECNICO").ifPresent(roles::add);
        } else {
            for (String code : roleCodes) {
                Rol rol = rolRepository.findByCodigo(code)
                        .orElseThrow(() -> new BadRequestException("Rol no válido: " + code));
                roles.add(rol);
            }
        }
        return roles;
    }
}
