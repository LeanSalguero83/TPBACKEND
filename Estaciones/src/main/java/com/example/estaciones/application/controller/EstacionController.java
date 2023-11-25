package com.example.estaciones.application.controller;

import com.example.estaciones.application.request.EstacionRequestDto;
import com.example.estaciones.application.ResponseHandler;
import com.example.estaciones.service.EstacionService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/estaciones")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)

public class EstacionController {

    EstacionService estacionService;

    @GetMapping
    public ResponseEntity<Object> listarEstaciones() {
        return ResponseHandler.success(estacionService.listarEstaciones());
    }

    @GetMapping("/cercana")
    public ResponseEntity<Object> encontrarEstacionCercana(@RequestParam Double latitud, @RequestParam Double longitud) {

        return ResponseHandler.success(estacionService.encontrarEstacionCercana(latitud, longitud));

    }

    @PostMapping
    public ResponseEntity<Object> agregarEstacion(@RequestBody EstacionRequestDto estacionRequestDTO) {

        return ResponseHandler.created(estacionService.agregarEstacion(estacionRequestDTO));

    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> findEstacionById(@PathVariable Integer id) {

        return ResponseHandler.success(estacionService.findEstacionById(id));


    }
}
