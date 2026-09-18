package com.abaxial.portal.equipment.entity;

import com.abaxial.portal.clients.entity.Cliente;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "equipos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
public class Equipo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;

    @Column(name = "codigo_inventario", nullable = false, unique = true, length = 50)
    private String codigoInventario;

    @Column(nullable = false, length = 50)
    private String tipo;

    @Column(length = 100)
    private String marca;

    @Column(length = 100)
    private String modelo;

    @Column(name = "numero_serie", length = 100)
    private String numeroSerie;

    @Column(name = "nombre_equipo", length = 150)
    private String nombreEquipo;

    @Column(length = 150)
    private String ubicacion;

    @Column(name = "ultima_revision", length = 100)
    private String ultimaRevision;

    @Column(length = 255)
    private String url;

    @Builder.Default
    @Column(nullable = false, length = 50)
    private String estado = "OPERATIVO";

    @Builder.Default
    @Column(name = "fecha_alta")
    private LocalDate fechaAlta = LocalDate.now();

    @Column(name = "fecha_baja")
    private LocalDate fechaBaja;

    @Column(columnDefinition = "TEXT")
    private String observaciones;

    @Builder.Default
    @Column(nullable = false)
    private Boolean activo = true;

    @Builder.Default
    @Column(name = "fecha_creacion", nullable = false)
    private LocalDateTime fechaCreacion = LocalDateTime.now();

    @Builder.Default
    @Column(name = "fecha_modificacion")
    private LocalDateTime fechaModificacion = LocalDateTime.now();
}
