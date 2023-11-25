package com.example.alquileres.model;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@RequiredArgsConstructor
@Entity
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = Tarifa.TABLE_NAME)
public class Tarifa {
    public static final String TABLE_NAME = "TARIFAS";

    @Id
    @Column(name = "ID")
    Integer id;

    @Column(name = "TIPO_TARIFA")
    Integer tipoTarifa = 1; // Valor por defecto 1

    @Column(name = "DEFINICION")
    String definicion = "S"; // Valor por defecto 'S'

    @Column(name = "DIA_SEMANA")
    Integer diaSemana;

    @Column(name = "DIA_MES")
    Integer diaMes;

    @Column(name = "MES")
    Integer mes;

    @Column(name = "ANIO")
    Integer anio;

    @Column(name = "MONTO_FIJO_ALQUILER")
    Double montoFijoAlquiler;

    @Column(name = "MONTO_MINUTO_FRACCION")
    Double montoMinutoFraccion;

    @Column(name = "MONTO_KM")
    Double montoKm;

    @Column(name = "MONTO_HORA")
    Double montoHora;
}

