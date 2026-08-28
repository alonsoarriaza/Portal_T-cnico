package com.abaxial.portal.contacts.dto;

import com.abaxial.portal.contacts.entity.Contacto;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContactoDTO {
    private Long id;
    private Long clienteId;
    private String clienteNombre;
    private String nombre;
    private String apellidos;
    private String cargo;
    private String email;
    private String telefono;
    private String telefonoFijo;
    private String observaciones;
    private boolean activo;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaModificacion;

    public static ContactoDTO fromEntity(Contacto c) {
        return ContactoDTO.builder()
                .id(c.getId())
                .clienteId(c.getCliente() != null ? c.getCliente().getId() : null)
                .clienteNombre(c.getCliente() != null ? c.getCliente().getNombre() : null)
                .nombre(c.getNombre())
                .apellidos(c.getApellidos())
                .cargo(c.getCargo())
                .email(c.getEmail())
                .telefono(c.getTelefono())
                .telefonoFijo(c.getTelefonoFijo())
                .observaciones(c.getObservaciones())
                .activo(Boolean.TRUE.equals(c.getActivo()))
                .fechaCreacion(c.getFechaCreacion())
                .fechaModificacion(c.getFechaModificacion())
                .build();
    }
}
