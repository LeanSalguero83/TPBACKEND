package com.example.estaciones.service;

import com.example.estaciones.application.response.EstacionResponseDto;
import com.example.estaciones.model.Estacion;
import org.springframework.stereotype.Service;

import java.util.function.Function;

@Service
public class EstacionDtoMapper implements Function<Estacion, EstacionResponseDto> {
    @Override
    public EstacionResponseDto apply(Estacion estacion) {
        EstacionResponseDto estacionResponseDto = new EstacionResponseDto();
        estacionResponseDto.setNombre(estacion.getNombre());
        estacionResponseDto.setLongitud(estacion.getLongitud());
        estacionResponseDto.setLatitud(estacion.getLatitud());
        return estacionResponseDto;
    }
}
