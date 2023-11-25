package com.example.estaciones.exceptions;

public class EstacionNotFoundException extends RuntimeException {

    public EstacionNotFoundException(String message) {
        super(message);
    }
}

