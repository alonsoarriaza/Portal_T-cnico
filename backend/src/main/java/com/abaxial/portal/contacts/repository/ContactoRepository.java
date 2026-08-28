package com.abaxial.portal.contacts.repository;

import com.abaxial.portal.contacts.entity.Contacto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ContactoRepository extends JpaRepository<Contacto, Long> {
    List<Contacto> findByClienteIdAndActivoTrueOrderByNombreAsc(Long clienteId);
    List<Contacto> findByClienteIdOrderByNombreAsc(Long clienteId);
}
