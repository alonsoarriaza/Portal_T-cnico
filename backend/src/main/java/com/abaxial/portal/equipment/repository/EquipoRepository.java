package com.abaxial.portal.equipment.repository;

import com.abaxial.portal.equipment.entity.Equipo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EquipoRepository extends JpaRepository<Equipo, Long> {

    Optional<Equipo> findByCodigoInventario(String codigoInventario);

    boolean existsByCodigoInventario(String codigoInventario);

    List<Equipo> findByClienteIdAndActivoTrueOrderByCodigoInventarioAsc(Long clienteId);

    List<Equipo> findByClienteIdOrderByCodigoInventarioAsc(Long clienteId);

    long countByActivo(boolean activo);

    long countByClienteIdAndActivoTrue(Long clienteId);

    @Query("SELECT e FROM Equipo e WHERE " +
           "(:clienteId IS NULL OR e.cliente.id = :clienteId) AND " +
           "(:includeInactive = true OR e.activo = true) AND " +
           "(:tipo IS NULL OR :tipo = '' OR e.tipo = :tipo) AND " +
           "(:estado IS NULL OR :estado = '' OR e.estado = :estado) AND " +
           "(:search IS NULL OR :search = '' OR LOWER(e.codigoInventario) LIKE LOWER(CONCAT('%', :search, '%')) " +
           "OR LOWER(e.marca) LIKE LOWER(CONCAT('%', :search, '%')) " +
           "OR LOWER(e.modelo) LIKE LOWER(CONCAT('%', :search, '%')) " +
           "OR LOWER(e.numeroSerie) LIKE LOWER(CONCAT('%', :search, '%')) " +
           "OR LOWER(e.cliente.nombre) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<Equipo> findAllFiltered(
            @Param("clienteId") Long clienteId,
            @Param("tipo") String tipo,
            @Param("estado") String estado,
            @Param("search") String search,
            @Param("includeInactive") boolean includeInactive,
            Pageable pageable
    );
}
