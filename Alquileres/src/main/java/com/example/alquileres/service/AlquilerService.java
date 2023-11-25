package com.example.alquileres.service;

import com.example.alquileres.dto.AlquilerResponseDto;
import com.example.alquileres.dto.FinalizarAlquilerRequestDTO;
import com.example.alquileres.dto.IniciarAlquilerRequestDTO;
import com.example.alquileres.model.Alquiler;

import java.util.List;

public interface AlquilerService {

    AlquilerResponseDto iniciarAlquiler(IniciarAlquilerRequestDTO requestDto);

    AlquilerResponseDto finalizarAlquiler(FinalizarAlquilerRequestDTO requestDto);

    List<AlquilerResponseDto> obtenerAlquileresPorEstado(Integer estado);

    List<AlquilerResponseDto> obtenerTodosLosAlquileres();

}
