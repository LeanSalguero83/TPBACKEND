package com.example.alquileres.repository;

import com.example.alquileres.model.Alquiler;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AlquilerRepository extends JpaRepository<Alquiler,Integer> {
    List<Alquiler> findByEstado(Integer estado);

}
