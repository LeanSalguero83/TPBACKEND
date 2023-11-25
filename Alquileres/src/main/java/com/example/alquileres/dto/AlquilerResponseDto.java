package com.example.alquileres.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AlquilerResponseDto {
    Integer id;
    String idCliente;
    Integer estado ;
    Integer estacionRetiro;
    Integer estacionDevolucion;
    LocalDateTime fechaHoraRetiro = LocalDateTime.now();
    LocalDateTime fechaHoraDevolucion;
    Double monto;

}
