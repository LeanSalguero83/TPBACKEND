package com.example.estaciones.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(EstacionNotFoundException.class)
    public ResponseEntity<?> handleEstacionNotFoundException(EstacionNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    }

    @ExceptionHandler(InvalidCoordinatesException.class)
    public ResponseEntity<?> handleInvalidCoordinatesException(InvalidCoordinatesException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    }
    @ExceptionHandler(InvalidStationIdException.class)
    public ResponseEntity<?> handleInvalidStationIdException(InvalidStationIdException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    }
    @ExceptionHandler(NombreEstacionRequeridoException.class)
    public ResponseEntity<?> handleNombreEstacionRequeridoException(NombreEstacionRequeridoException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    }

    @ExceptionHandler(CoordenadasEstacionRequeridasException.class)
    public ResponseEntity<?> handleCoordenadasEstacionRequeridasException(CoordenadasEstacionRequeridasException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    }


}
