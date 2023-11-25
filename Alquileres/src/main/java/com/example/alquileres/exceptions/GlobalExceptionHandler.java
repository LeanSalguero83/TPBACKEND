package com.example.alquileres.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(EstacionRetiroNotFoundException.class)
    public ResponseEntity<?> handleEstacionRetiroNotFoundException(EstacionRetiroNotFoundException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    }

    @ExceptionHandler(EstacionDevolucionNotFoundException.class)
    public ResponseEntity<?> handleEstacionDevolucionNotFoundException(EstacionDevolucionNotFoundException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    }

    @ExceptionHandler(TarifaNotFoundException.class)
    public ResponseEntity<?> handleTarifaNotFoundException(TarifaNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    }

    @ExceptionHandler(AlquilerNotFoundException.class)
    public ResponseEntity<?> handleAlquilerNotFoundException(AlquilerNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    }

    @ExceptionHandler(MonedaConversionException.class)
    public ResponseEntity<?> handleMonedaConversionException(MonedaConversionException e) {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(e.getMessage());
    }

    @ExceptionHandler(TarifaNoAsignadaException.class)
    public ResponseEntity<?> handleTarifaNoAsignadaException(TarifaNoAsignadaException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    }

    @ExceptionHandler(CalculoDistanciaException.class)
    public ResponseEntity<?> handleCalculoDistanciaException(CalculoDistanciaException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    }

    @ExceptionHandler(CalculoMontoException.class)
    public ResponseEntity<?> handleCalculoMontoException(CalculoMontoException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    }

    @ExceptionHandler(AlquilerYaFinalizadoException.class)
    public ResponseEntity<?> handleAlquilerYaFinalizadoException(AlquilerYaFinalizadoException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    }

    @ExceptionHandler(EstadoNoValidoExcepction.class)
    public ResponseEntity<?> handleEstadoNoValidoExcepction(EstadoNoValidoExcepction e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    }


}
