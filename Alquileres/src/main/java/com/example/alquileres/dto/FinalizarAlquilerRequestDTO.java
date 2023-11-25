package com.example.alquileres.dto;

import lombok.Data;

@Data
public class FinalizarAlquilerRequestDTO {
    private Integer idAlquiler;
    private Integer idEstacionDevolucion;
    private String monedaDeseada;

}
