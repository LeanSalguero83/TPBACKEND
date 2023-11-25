package com.example.alquileres.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import java.time.LocalDateTime;

@Entity
@Table(name = Alquiler.TABLE_NAME)
@FieldDefaults(level = AccessLevel.PRIVATE)
@Data
public class Alquiler {

    public static final String TABLE_NAME = "ALQUILERES";

    @Id
    @Column(name = "ID")
    Integer id;

    @Column(name = "ID_CLIENTE", length = 50)
    String idCliente;

    @Column(name = "ESTADO")
    Integer estado = 1;

    @Column(name = "ESTACION_RETIRO")
    Integer estacionRetiro;

    @Column(name = "ESTACION_DEVOLUCION")
    Integer estacionDevolucion;

    @Convert(converter = LocalDateTimeConverter.class)
    @Column(name = "FECHA_HORA_RETIRO")
    LocalDateTime fechaHoraRetiro = LocalDateTime.now();


    @Convert(converter = LocalDateTimeConverter.class)
    @Column(name = "FECHA_HORA_DEVOLUCION")
    LocalDateTime fechaHoraDevolucion;

    @Column(name = "MONTO")
    Double monto;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_TARIFA")
    @JsonIgnore
    Tarifa tarifa;
}
