package com.abaxial.portal.clients.repository;

import com.abaxial.portal.clients.entity.Cliente;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long> {

    Optional<Cliente> findByCodigo(String codigo);

    Optional<Cliente> findByCodigoIgnoreCase(String codigo);

    Optional<Cliente> findByNifCif(String nifCif);

    List<Cliente> findByNifCifIgnoreCase(String nifCif);

    Optional<Cliente> findByNombreIgnoreCase(String nombre);

    boolean existsByCodigo(String codigo);

    boolean existsByNifCif(String nifCif);

    @Query("SELECT c FROM Cliente c WHERE " +
           "(:includeInactive = true OR :estado = 'BAJA' OR c.activo = true) AND " +
           "(:estado IS NULL OR :estado = '' OR c.estado = :estado) AND " +
           "(:mantenimiento IS NULL OR :mantenimiento = '' OR c.mantenimiento = :mantenimiento) AND " +
           "(:provincia IS NULL OR :provincia = '' OR LOWER(c.provincia) LIKE LOWER(CONCAT('%', :provincia, '%'))) AND " +
           "(:search IS NULL OR :search = '' OR LOWER(c.nombre) LIKE LOWER(CONCAT('%', :search, '%')) " +
           "OR LOWER(c.codigo) LIKE LOWER(CONCAT('%', :search, '%')) " +
           "OR LOWER(c.nifCif) LIKE LOWER(CONCAT('%', :search, '%')) " +
           "OR LOWER(c.gerente) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<Cliente> findAllFiltered(
            @Param("search") String search,
            @Param("estado") String estado,
            @Param("mantenimiento") String mantenimiento,
            @Param("provincia") String provincia,
            @Param("includeInactive") boolean includeInactive,
            Pageable pageable
    );

    long countByActivo(boolean activo);
    long countByEstadoAndActivo(String estado, boolean activo);
}
