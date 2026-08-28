package com.abaxial.portal.documents.repository;

import com.abaxial.portal.documents.entity.DocumentoVersion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DocumentoVersionRepository extends JpaRepository<DocumentoVersion, Long> {

    List<DocumentoVersion> findByDocumentoIdOrderByVersionDesc(Long documentoId);

    Optional<DocumentoVersion> findByDocumentoIdAndVersion(Long documentoId, Integer version);

    Optional<DocumentoVersion> findByNombreInterno(String nombreInterno);
}
