package com.abaxial.portal.security;

import com.abaxial.portal.users.entity.Usuario;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@Getter
@AllArgsConstructor
public class CustomUserDetails implements UserDetails {

    private final Long id;
    private final String username;
    private final String email;
    private final String password;
    private final String nombreCompleto;
    private final boolean activo;
    private final Collection<? extends GrantedAuthority> authorities;

    public static CustomUserDetails build(Usuario usuario) {
        Set<GrantedAuthority> authorities = new HashSet<>();

        if (usuario.getRoles() != null) {
            usuario.getRoles().forEach(rol -> {
                authorities.add(new SimpleGrantedAuthority("ROLE_" + rol.getCodigo()));
                if (rol.getPermisos() != null) {
                    rol.getPermisos().forEach(permiso -> {
                        authorities.add(new SimpleGrantedAuthority(permiso.getCodigo()));
                    });
                }
            });
        }

        return new CustomUserDetails(
                usuario.getId(),
                usuario.getUsername(),
                usuario.getEmail(),
                usuario.getPasswordHash(),
                usuario.getNombreCompleto(),
                Boolean.TRUE.equals(usuario.getActivo()),
                authorities
        );
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return activo;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return activo;
    }
}
