package com.example.alquileres.service;

import com.example.alquileres.model.Alquiler;

import java.util.List;

public interface AlquilerService {

    void iniciarAlquiler(Integer idEstacion, String idCliente);

    void finalizarAlquiler(Integer idAlquiler, Integer idEstacionDevolucion, String monedaDeseada);

    List<Alquiler> obtenerAlquileresPorEstado(Integer estado);

    List<Alquiler> obtenerTodosLosAlquileres();

}
