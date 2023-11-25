package com.example.alquileres.exceptions;

public class AlquilerNotFoundException extends RuntimeException {
    public AlquilerNotFoundException(String message) {
        super(message);
    }
}
