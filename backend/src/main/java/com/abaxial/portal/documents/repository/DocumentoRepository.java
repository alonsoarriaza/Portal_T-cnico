package com.abaxial.portal.documents.repository;

import com.abaxial.portal.documents.entity.Documento;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DocumentoRepository extends JpaRepository<Documento, Long> {

    List<Documento> findByClienteIdAndActivoTrueOrderByNombreOriginalAsc(Long clienteId);

    List<Documento> findByClienteIdOrderByNombreOriginalAsc(Long clienteId);

    List<Documento> findByClienteIdAndActivoTrueOrderByFechaCreacionDesc(Long clienteId);

    List<Documento> findByClienteIdOrderByFechaCreacionDesc(Long clienteId);

    Optional<Documento> findByClienteIdAndNombreOriginalAndActivoTrue(Long clienteId, String nombreOriginal);

    long countByActivo(boolean activo);

    @Query("SELECT d FROM Documento d WHERE " +
           "(:clienteId IS NULL OR d.cliente.id = :clienteId) AND " +
           "(:includeInactive = true OR d.activo = true) AND " +
           "(:categoria IS NULL OR :categoria = '' OR d.categoria = :categoria) AND " +
           "(:search IS NULL OR :search = '' OR LOWER(d.nombreOriginal) LIKE LOWER(CONCAT('%', :search, '%')) " +
           "OR LOWER(d.descripcion) LIKE LOWER(CONCAT('%', :search, '%')) " +
           "OR LOWER(d.cliente.nombre) LIKE LOWER(CONCAT('%', :search, '%'))) " +
           "ORDER BY LOWER(d.nombreOriginal) ASC")
    Page<Documento> findAllFiltered(
            @Param("clienteId") Long clienteId,
            @Param("categoria") String categoria,
            @Param("search") String search,
            @Param("includeInactive") boolean includeInactive,
            Pageable pageable
    );
}
