package com.example.estaciones.application.request;

import lombok.Data;

@Data
public class EstacionRequestDto {
    private String nombre;
    private Double latitud;
    private Double longitud;
}
