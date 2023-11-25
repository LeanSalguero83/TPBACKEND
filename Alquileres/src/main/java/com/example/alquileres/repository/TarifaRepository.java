package com.example.alquileres.repository;

import com.example.alquileres.model.Tarifa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TarifaRepository extends JpaRepository<Tarifa,Integer> {
    @Query("SELECT t FROM Tarifa t WHERE (t.diaSemana = ?1 AND t.definicion = 'S') OR (t.diaMes = ?2 AND t.mes = ?3 AND t.anio = ?4 AND t.definicion = 'C')")
    List<Tarifa> encontrarTarifasPorFecha(int diaSemana, int diaMes, int mes, int anio);


}