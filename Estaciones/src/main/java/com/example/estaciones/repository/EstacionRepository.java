package com.example.estaciones.repository;

import com.example.estaciones.model.Estacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
public interface EstacionRepository extends JpaRepository<Estacion,Integer> {

    @Query(value = "SELECT * FROM ESTACIONES ORDER BY SQRT(POWER((LATITUD - ?1) * 110000, 2) + POWER((LONGITUD - ?2) * 110000, 2)) ASC LIMIT 1", nativeQuery = true)
    Optional<Estacion> encontrarMasCercana(Double lat, Double lon);


}
