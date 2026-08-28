package com.abaxial.portal.services.repository;

import com.abaxial.portal.services.entity.Servicio;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ServicioRepository extends JpaRepository<Servicio, Long> {

    List<Servicio> findByClienteIdAndActivoTrueOrderByNombreAsc(Long clienteId);

    List<Servicio> findByClienteIdOrderByNombreAsc(Long clienteId);

    long countByActivo(boolean activo);

    @Query("SELECT s FROM Servicio s WHERE " +
           "(:clienteId IS NULL OR s.cliente.id = :clienteId) AND " +
           "(:includeInactive = true OR s.activo = true) AND " +
           "(:estado IS NULL OR :estado = '' OR s.estado = :estado) AND " +
           "(:search IS NULL OR :search = '' OR LOWER(s.nombre) LIKE LOWER(CONCAT('%', :search, '%')) " +
           "OR LOWER(s.descripcion) LIKE LOWER(CONCAT('%', :search, '%')) " +
           "OR LOWER(s.cliente.nombre) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<Servicio> findAllFiltered(
            @Param("clienteId") Long clienteId,
            @Param("estado") String estado,
            @Param("search") String search,
            @Param("includeInactive") boolean includeInactive,
            Pageable pageable
    );
}
