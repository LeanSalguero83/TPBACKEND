package com.example.alquileres.application.controller;

import com.example.alquileres.application.ResponseHandler;
import com.example.alquileres.dto.FinalizarAlquilerRequestDTO;
import com.example.alquileres.dto.IniciarAlquilerRequestDTO;
import com.example.alquileres.service.AlquilerService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/alquileres")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AlquilerController {

    AlquilerService alquilerService;

    @PostMapping("/iniciar")
    public ResponseEntity<Object> iniciarAlquiler(@RequestBody IniciarAlquilerRequestDTO requestDTO) {
        return ResponseHandler.created(alquilerService.iniciarAlquiler(requestDTO));
    }

    @PostMapping("/finalizar")
    public ResponseEntity<Object> finalizarAlquiler(@RequestBody FinalizarAlquilerRequestDTO requestDTO) {
        return ResponseHandler.success(alquilerService.finalizarAlquiler(requestDTO));
    }

    @GetMapping
    public ResponseEntity<Object> obtenerTodosLosAlquileres() {
        return ResponseHandler.success(alquilerService.obtenerTodosLosAlquileres());
    }

    @GetMapping("/estado/{estado}")
    public ResponseEntity<Object> obtenerAlquileresPorEstado(@PathVariable Integer estado) {
        return ResponseHandler.success(alquilerService.obtenerAlquileresPorEstado(estado));
    }


}
