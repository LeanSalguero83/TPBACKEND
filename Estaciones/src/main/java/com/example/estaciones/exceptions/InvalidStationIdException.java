package com.example.estaciones.exceptions;

public class InvalidStationIdException extends RuntimeException {
    public InvalidStationIdException(String message) {
        super(message);
    }
}

