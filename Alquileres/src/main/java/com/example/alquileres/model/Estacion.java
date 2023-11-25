package com.example.alquileres.model;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Data
public class Estacion {
    String nombre;
    Double latitud;
    Double longitud;

}
