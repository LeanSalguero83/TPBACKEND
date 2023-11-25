package com.example.alquileres.exceptions;

public class TarifaNotFoundException extends RuntimeException {
    public TarifaNotFoundException(String message) {
        super(message);
    }
}
