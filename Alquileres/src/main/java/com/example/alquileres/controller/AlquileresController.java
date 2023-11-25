package com.example.alquileres.controller;
import com.example.alquileres.model.Alquiler;
import com.example.alquileres.service.AlquilerService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@RestController
@RequestMapping("/api/alquileres")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)


public class AlquileresController {
    AlquilerService alquilerService;

    @PostMapping("/iniciar")
    public ResponseEntity<Void> iniciarAlquiler(@RequestParam Integer idEstacion,
                                                @RequestParam String idCliente
                                                ) {
        alquilerService.iniciarAlquiler(idEstacion, idCliente);
        return ResponseEntity.ok().build();
    }


    @PostMapping("/finalizar")
    public ResponseEntity<Void> finalizarAlquiler(@RequestParam Integer idAlquiler,
                                                  @RequestParam Integer idEstacionDevolucion,
                                                  @RequestParam(required = false) String monedaDeseada) {
        alquilerService.finalizarAlquiler(idAlquiler, idEstacionDevolucion, monedaDeseada);
        return ResponseEntity.ok().build();
    }
    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<Alquiler>> obtenerAlquileresPorEstado(@PathVariable Integer estado) {
        List<Alquiler> alquileres = alquilerService.obtenerAlquileresPorEstado(estado);
        return ResponseEntity.ok(alquileres);
    }

    @GetMapping
    public ResponseEntity<List<Alquiler>> obtenerTodosLosAlquileres() {
        List<Alquiler> alquileres = alquilerService.obtenerTodosLosAlquileres();
        return ResponseEntity.ok(alquileres);
    }

}
