package com.example.estaciones.service;

import com.example.estaciones.application.request.EstacionRequestDto;
import com.example.estaciones.exceptions.*;
import com.example.estaciones.model.Estacion;
import com.example.estaciones.repository.EstacionRepository;
import com.example.estaciones.repository.IdentifierRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import com.example.estaciones.application.response.EstacionResponseDto;
import java.time.LocalDateTime;

import java.util.List;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class EstacionServiceImpl implements EstacionService {

    EstacionRepository estacionRepository;
    IdentifierRepository identifierRepository;
    EstacionDtoMapper estacionDtoMapper;

    @Override
    public List<EstacionResponseDto> listarEstaciones() {
       List<Estacion> estaciones =   estacionRepository.findAll();
       return  estaciones
               .stream()
               .map(estacionDtoMapper)
               .toList();
    }

    @Override
    public EstacionResponseDto encontrarEstacionCercana(Double latitud, Double longitud) {
        if (latitud == null || longitud == null) {
            throw new InvalidCoordinatesException("Coordenadas inválidas");

        }

        Estacion estacion = estacionRepository.encontrarMasCercana(latitud, longitud)
                .orElseThrow(() -> new EstacionNotFoundException("No se encontró una estación cercana"));

        return estacionDtoMapper.apply(estacion);
    }

    @Override
    public EstacionResponseDto agregarEstacion(EstacionRequestDto estacionRequestDTO) {
        if (estacionRequestDTO.getNombre() == null || estacionRequestDTO.getNombre().isEmpty()) {
            throw new NombreEstacionRequeridoException();

        }
        if (estacionRequestDTO.getLatitud() == null || estacionRequestDTO.getLongitud() == null) {
            throw new CoordenadasEstacionRequeridasException();

        }

        Estacion estacion = new Estacion();
        estacion.setNombre(estacionRequestDTO.getNombre());
        estacion.setLatitud(estacionRequestDTO.getLatitud());
        estacion.setLongitud(estacionRequestDTO.getLongitud());
        estacion.setStationId(identifierRepository.nextValue(Estacion.TABLE_NAME));
        estacion.setFechaHoraCreacion(LocalDateTime.now());
        Estacion savedEstacion = estacionRepository.save(estacion);
        return estacionDtoMapper.apply(savedEstacion);
    }


    @Override
    public EstacionResponseDto findEstacionById(Integer id) {
        if (id == null) {
            throw new InvalidStationIdException("ID de estación inválido");
        }
        return estacionRepository.findById(id)
                .map(estacionDtoMapper)
                .orElseThrow(() -> new EstacionNotFoundException("Estación no encontrada"));
    }


}
