package com.example.estaciones.application.response;

import lombok.Data;

@Data
public class EstacionResponseDto {
    private String nombre;
    private Double latitud;
    private Double longitud;
}
