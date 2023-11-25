package com.example.estaciones.exceptions;

public class NombreEstacionRequeridoException extends RuntimeException {
    public NombreEstacionRequeridoException() {
        super("El nombre de la estación es requerido.");
    }
}