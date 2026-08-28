package com.abaxial.portal.events.entity;

import com.abaxial.portal.clients.entity.Cliente;
import com.abaxial.portal.users.entity.Usuario;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "eventos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
public class Evento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id")
    private Cliente cliente;

    @Column(nullable = false, length = 150)
    private String titulo;

    @Column(columnDefinition = "TEXT")
    private String descripcion;

    @Column(name = "fecha_inicio", nullable = false)
    private LocalDateTime fechaInicio;

    @Column(name = "fecha_fin")
    private LocalDateTime fechaFin;

    @Builder.Default
    @Column(length = 30)
    private String tipo = "PUNTUAL";

    @Column(length = 50)
    private String recurrencia;

    @Column(name = "fecha_fin_recurrencia")
    private LocalDateTime fechaFinRecurrencia;

    @Column(name = "dias_semana", length = 50)
    private String diasSemana;

    @Column(name = "dia_mes")
    private Integer diaMes;

    @Column(name = "evento_padre_id")
    private Long eventoPadreId;

    @Column(name = "fecha_original_ocurrencia")
    private LocalDateTime fechaOriginalOcurrencia;

    @Builder.Default
    @Column(name = "es_excepcion")
    private Boolean esExcepcion = false;

    @Column(name = "fechas_excluidas", columnDefinition = "TEXT")
    private String fechasExcluidas;

    @Builder.Default
    @Column(nullable = false, length = 20)
    private String prioridad = "MEDIA";

    @Builder.Default
    @Column(nullable = false, length = 30)
    private String estado = "PENDIENTE";

    @Builder.Default
    @Column(nullable = false, length = 20)
    private String visibilidad = "COMPARTIDO";

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
