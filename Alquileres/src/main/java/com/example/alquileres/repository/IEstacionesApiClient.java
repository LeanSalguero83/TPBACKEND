
package com.example.alquileres.repository;

import com.example.alquileres.model.Estacion;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

    @FeignClient(name = "estacionesapi", url="http://localhost:9001/estaciones")
public interface IEstacionesApiClient {
    @GetMapping("/{id}")
    Estacion getEstacionById(@PathVariable Integer id);
}

