package com.example.estaciones.exceptions;

public class CoordenadasEstacionRequeridasException extends RuntimeException {
    public CoordenadasEstacionRequeridasException() {
        super("Las coordenadas de la estación son requeridas.");
    }
}
