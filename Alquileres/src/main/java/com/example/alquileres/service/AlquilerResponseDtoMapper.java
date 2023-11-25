package com.example.alquileres.service;

import com.example.alquileres.dto.AlquilerResponseDto;
import com.example.alquileres.model.Alquiler;
import org.springframework.stereotype.Service;

import java.util.function.Function;

@Service
public class AlquilerResponseDtoMapper implements Function<Alquiler, AlquilerResponseDto> {
    @Override
    public AlquilerResponseDto apply(Alquiler alquiler) {
        AlquilerResponseDto alquilerResponseDto = new AlquilerResponseDto();
        alquilerResponseDto.setId(alquiler.getId());
        alquilerResponseDto.setEstado(alquiler.getEstado());
        alquilerResponseDto.setEstacionRetiro(alquilerResponseDto.getEstacionRetiro());
        alquilerResponseDto.setEstacionDevolucion(alquiler.getEstacionDevolucion());
        alquilerResponseDto.setIdCliente(alquilerResponseDto.getIdCliente());
        alquilerResponseDto.setFechaHoraRetiro(alquiler.getFechaHoraRetiro());
        alquilerResponseDto.setFechaHoraDevolucion(alquiler.getFechaHoraDevolucion());
        alquilerResponseDto.setMonto(alquiler.getMonto());
        return alquilerResponseDto;

    }
}
