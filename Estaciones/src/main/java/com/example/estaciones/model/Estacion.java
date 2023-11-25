package com.example.estaciones.model;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Entity
@Table(name = Estacion.TABLE_NAME)
@FieldDefaults(level = AccessLevel.PRIVATE)
@Data
public class Estacion {
    public static final String TABLE_NAME = "ESTACIONES";
    @Id
    @Column(name = "ID")
    Integer stationId;

    @Column(name = "NOMBRE", length = 100)
    String nombre;

    @Convert(converter = LocalDateTimeConverter.class)
    @Column(name = "FECHA_HORA_CREACION")
    LocalDateTime fechaHoraCreacion;

    @Column(name = "LATITUD")
    Double latitud;

    @Column(name = "LONGITUD")
    Double longitud;


}
