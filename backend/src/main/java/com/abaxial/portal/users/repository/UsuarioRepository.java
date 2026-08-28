package com.abaxial.portal.users.repository;

import com.abaxial.portal.users.entity.Usuario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByUsername(String username);

    Optional<Usuario> findByEmail(String email);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    @Query("SELECT u FROM Usuario u WHERE (:includeInactive = true OR u.activo = true) " +
           "AND (:search IS NULL OR :search = '' OR LOWER(u.username) LIKE LOWER(CONCAT('%', :search, '%')) " +
           "OR LOWER(u.nombre) LIKE LOWER(CONCAT('%', :search, '%')) " +
           "OR LOWER(u.apellidos) LIKE LOWER(CONCAT('%', :search, '%')) " +
           "OR LOWER(u.email) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<Usuario> findAllFiltered(@Param("search") String search, 
                                  @Param("includeInactive") boolean includeInactive, 
                                  Pageable pageable);

    long countByActivo(boolean activo);
}
