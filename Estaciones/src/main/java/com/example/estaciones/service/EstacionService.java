package com.example.estaciones.service;

import com.example.estaciones.application.request.EstacionRequestDto;
import com.example.estaciones.application.response.EstacionResponseDto;

import java.util.List;

public interface EstacionService {

    List<EstacionResponseDto> listarEstaciones();

    EstacionResponseDto encontrarEstacionCercana(Double latitud, Double longitud);

    EstacionResponseDto agregarEstacion(EstacionRequestDto estacionRequestDTO);

    EstacionResponseDto findEstacionById(Integer id);


}
