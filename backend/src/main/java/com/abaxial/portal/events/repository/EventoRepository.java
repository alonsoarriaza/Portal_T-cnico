package com.abaxial.portal.events.repository;

import com.abaxial.portal.events.entity.Evento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface EventoRepository extends JpaRepository<Evento, Long>, JpaSpecificationExecutor<Evento> {

    List<Evento> findByClienteIdAndActivoTrueOrderByFechaInicioAsc(Long clienteId);

    List<Evento> findTop5ByActivoTrueAndFechaInicioGreaterThanEqualOrderByFechaInicioAsc(LocalDateTime now);

    long countByActivoAndEstado(boolean activo, String estado);

    long countByActivo(boolean activo);

    List<Evento> findByEventoPadreIdAndActivoTrue(Long eventoPadreId);

    List<Evento> findByEventoPadreId(Long eventoPadreId);
}
