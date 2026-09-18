package com.abaxial.portal.clients.importer.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ParsedClient {
    private String codigo;
    private String nifCif;
    private String nombre;
    private String razonSocial;
    private String estado;
    private String mantenimiento;
    private String diaMantenimiento;
    private String tecnico;
    private String visitasMes;
    private Integer equiposDeclarados;
    private Integer websRegistradas;
    private String direccion;
    private String poblacion;
    private String provincia;
    private String gerente;
    private String emailGerente;
    private String personaTic;
    private String emailPersonaTic;
    private String telefono;
    private String fechaAltaStr;

    @Builder.Default
    private List<ParsedEquipment> equipments = new ArrayList<>();

    @Builder.Default
    private List<ParsedContact> contacts = new ArrayList<>();
}
