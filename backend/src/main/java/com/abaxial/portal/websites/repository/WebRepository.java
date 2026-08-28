package com.abaxial.portal.websites.repository;

import com.abaxial.portal.websites.entity.Web;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WebRepository extends JpaRepository<Web, Long> {

    List<Web> findByClienteIdAndActivoTrueOrderByNombreAsc(Long clienteId);

    List<Web> findByClienteIdOrderByNombreAsc(Long clienteId);

    long countByActivo(boolean activo);

    @Query("SELECT w FROM Web w WHERE " +
           "(:clienteId IS NULL OR w.cliente.id = :clienteId) AND " +
           "(:includeInactive = true OR w.activo = true) AND " +
           "(:estado IS NULL OR :estado = '' OR w.estado = :estado) AND " +
           "(:search IS NULL OR :search = '' OR LOWER(w.nombre) LIKE LOWER(CONCAT('%', :search, '%')) " +
           "OR LOWER(w.url) LIKE LOWER(CONCAT('%', :search, '%')) " +
           "OR LOWER(w.cliente.nombre) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<Web> findAllFiltered(
            @Param("clienteId") Long clienteId,
            @Param("estado") String estado,
            @Param("search") String search,
            @Param("includeInactive") boolean includeInactive,
            Pageable pageable
    );
}
