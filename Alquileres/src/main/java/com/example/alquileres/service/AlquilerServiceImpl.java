package com.example.alquileres.service;

import com.example.alquileres.dto.AlquilerResponseDto;
import com.example.alquileres.dto.FinalizarAlquilerRequestDTO;
import com.example.alquileres.dto.IniciarAlquilerRequestDTO;
import com.example.alquileres.exceptions.*;
import com.example.alquileres.model.Alquiler;
import com.example.alquileres.model.Tarifa;
import com.example.alquileres.repository.*;
import feign.FeignException;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class AlquilerServiceImpl implements AlquilerService {

    AlquilerRepository alquilerRepository;
    IdentifierRepository identifierRepository;
    IEstacionesApiClient estacionesApiClient;
    TarifaRepository tarifaRepository;
    ExchangeRateApiClient exchangeRateApiClient;
    AlquilerResponseDtoMapper alquilerResponseDtoMapper;
    static final Logger logger = LoggerFactory.getLogger(AlquilerServiceImpl.class);
    @Transactional
    public AlquilerResponseDto iniciarAlquiler(IniciarAlquilerRequestDTO requestDto) {
        // Verificar que la estación existe
        var estacion = estacionesApiClient.getEstacionById(requestDto.getIdEstacion());
        if (estacion == null) {
            throw new EstacionRetiroNotFoundException("La estación con ID " + requestDto.getIdEstacion() + " no existe.");
        }

        // obtengo la tarifa en base a la fecha actual
        LocalDateTime ahora = LocalDateTime.now();
        List<Tarifa> tarifas = tarifaRepository.encontrarTarifasPorFecha(
                ahora.getDayOfWeek().getValue(),
                ahora.getDayOfMonth(),
                ahora.getMonthValue(),
                ahora.getYear()
        );

        Tarifa tarifa = tarifas.stream()
                .filter(t -> t.getDefinicion().equals("C"))
                .findFirst()
                .orElse(tarifas.stream()
                        .filter(t -> t.getDefinicion().equals("S"))
                        .findFirst()
                        .orElse(null));

        if (tarifa == null) {
            throw new TarifaNotFoundException("No se encontró una tarifa adecuada para la fecha y hora actual.");
        }

        // Crear un nuevo objeto alquiler con los datos de inicio y empiezo a setterlo.
        Alquiler alquiler = new Alquiler();
        alquiler.setId(identifierRepository.nextValue(Alquiler.TABLE_NAME));
        alquiler.setIdCliente(requestDto.getIdCliente());
        alquiler.setEstado(1); // Estado "Iniciado"
        alquiler.setEstacionRetiro(requestDto.getIdEstacion());
        alquiler.setFechaHoraRetiro(java.time.LocalDateTime.now());
        alquiler.setTarifa(tarifa);
        // Los campos de devolución y monto se dejan nulos, se actualizarán al finalizar el alquiler.

        // Guardar el nuevo alquiler
        alquilerRepository.save(alquiler);

        return alquilerResponseDtoMapper.apply(alquiler);


    }

    @Transactional
    public AlquilerResponseDto finalizarAlquiler(FinalizarAlquilerRequestDTO requestDto) {
        logger.info("Iniciando el proceso de finalizar alquiler para el id: {}", requestDto.getIdAlquiler());


        // Obtener alquiler
        Alquiler alquiler = alquilerRepository.findById(requestDto.getIdAlquiler())
                .orElseThrow(() -> new AlquilerNotFoundException("Alquiler no encontrado"));

        logger.info("Alquiler obtenido para el id: {}", alquiler.getId());

        // Verificar estado del alquiler
        if (alquiler.getEstado() == 2) {
            throw new AlquilerNotFoundException("El alquiler ya ha sido finalizado.");
        }

        // Obtener estación de retiro y devolución
        var estacionRetiro = estacionesApiClient.getEstacionById(alquiler.getEstacionRetiro());
        if (estacionRetiro == null) {
            throw new EstacionRetiroNotFoundException("La estación de retiro con ID " + alquiler.getEstacionRetiro() + " no existe.");
        }
        var estacionDevolucion = estacionesApiClient.getEstacionById(requestDto.getIdEstacionDevolucion());
        if (estacionDevolucion == null) {
            throw new EstacionDevolucionNotFoundException("La estación de devolución con ID " + requestDto.getIdEstacionDevolucion() + " no existe.");
        }

        // Calcular distancia
        double distancia = calcularDistancia(
                estacionRetiro.getLatitud(),
                estacionRetiro.getLongitud(),
                estacionDevolucion.getLatitud(),
                estacionDevolucion.getLongitud()
        );

        // Obtener la tarifa directamente del alquiler
        Tarifa tarifa = alquiler.getTarifa();
        if (tarifa == null) {
            throw new TarifaNoAsignadaException("No se ha asignado tarifa al alquiler con ID " + alquiler.getId());
        }

        // Calcular monto total
        double montoTotal = calcularMontoTotal(
                alquiler.getFechaHoraRetiro(),
                LocalDateTime.now(),
                tarifa,
                distancia
        );

        logger.info("Monto total calculado para el alquiler id: {} es: {}", alquiler.getId(), montoTotal);


        // Convertir moneda si es necesario
        if (requestDto.getMonedaDeseada() != null && !requestDto.getMonedaDeseada().isEmpty()) {
            try {
                montoTotal = convertirMoneda(montoTotal, "ARS", requestDto.getMonedaDeseada());
                logger.info("Monto convertido para el alquiler id: {} en moneda {}: {}", alquiler.getId(), requestDto.getMonedaDeseada(), montoTotal);
            } catch (IllegalStateException e) {
                logger.error("Error al convertir la moneda para el alquiler id: {}", alquiler.getId(), e);
                throw new MonedaConversionException("No se pudo convertir la moneda: " + e.getMessage());
            }
        }

        // Actualizar alquiler
        alquiler.setEstado(2); // Estado "Finalizado"
        alquiler.setEstacionDevolucion(requestDto.getIdEstacionDevolucion());
        alquiler.setFechaHoraDevolucion(LocalDateTime.now());
        alquiler.setMonto(montoTotal);

        // Guardar el alquiler actualizado
        alquilerRepository.save(alquiler);
        logger.info("Alquiler finalizado y guardado con id: {}", alquiler.getId());

        return  alquilerResponseDtoMapper.apply(alquiler);

    }

    public double calcularDistancia(double lat1, double lon1, double lat2, double lon2) {
        try {
            // Validaciones de latitud y longitud
            if(lat1 < -90 || lat1 > 90 || lat2 < -90 || lat2 > 90 || lon1 < -180 || lon1 > 180 || lon2 < -180 || lon2 > 180) {
                throw new CalculoDistanciaException("Coordenadas geográficas inválidas proporcionadas.");
            }

            double latitudDistancia = Math.toRadians(lat2 - lat1);
            double longitudDistancia = Math.toRadians(lon2 - lon1);

            // Cálculo de distancia (podría incluir lógica adicional o llamadas a servicios externos)
            return Math.sqrt(Math.pow(latitudDistancia, 2) + Math.pow(longitudDistancia, 2)) * 110000;
        } catch (Exception e) {
            throw new CalculoDistanciaException("Error al calcular la distancia: " + e.getMessage());
        }
    }

    public double calcularMontoTotal(LocalDateTime inicio, LocalDateTime fin, Tarifa tarifa, double distancia) {
        if (inicio == null || fin == null) {
            throw new CalculoMontoException("Las fechas de inicio y fin no pueden ser nulas");
        }
        // Calcular tiempo de alquiler en minutos y horas
        long minutosTotales = ChronoUnit.MINUTES.between(inicio, fin);
        long horasCompletas = minutosTotales / 60;
        long minutosFraccion = minutosTotales % 60;

        // Aplicar tarifa según tiempo y distancia
        double montoTotal = tarifa.getMontoFijoAlquiler();
        montoTotal += minutosFraccion > 30 ? (horasCompletas + 1) * tarifa.getMontoHora() : horasCompletas * tarifa.getMontoHora() + minutosFraccion * tarifa.getMontoMinutoFraccion();
        montoTotal += distancia / 1000 * tarifa.getMontoKm(); // Dividir por 1000 para convertir metros en KM

        return montoTotal;
    }

    public double convertirMoneda(double monto, String baseCurrency, String monedaDeseada) {
        try {
            Map<String, Object> response = exchangeRateApiClient.getExchangeRate(baseCurrency);
            Map<String, Object> rates = (Map<String, Object>) response.get("rates");
            Object tasaDeCambioObjeto = rates.get(monedaDeseada);
            if (tasaDeCambioObjeto == null) {
                throw new MonedaConversionException("Tasa de cambio no encontrada para la moneda: " + monedaDeseada);
            }
            double tasaDeCambio;
            if (tasaDeCambioObjeto instanceof Integer) {
                tasaDeCambio = ((Integer) tasaDeCambioObjeto).doubleValue();
            } else if (tasaDeCambioObjeto instanceof Double) {
                tasaDeCambio = (Double) tasaDeCambioObjeto;
            } else {
                throw new MonedaConversionException("Formato de tasa de cambio no esperado para la moneda: " + monedaDeseada);
            }
            return monto * tasaDeCambio;
        } catch (FeignException e) {
            logger.error("El servicio de conversión de moneda no está disponible en este momento", e);
            throw new MonedaConversionException("El servicio de conversión de moneda no está disponible en este momento");
        } catch (Exception e) {
            logger.error("Ocurrió un error al realizar la conversión de moneda", e);
            throw new MonedaConversionException("Ocurrió un error al realizar la conversión de moneda");
        }
    }


    public List<AlquilerResponseDto> obtenerAlquileresPorEstado(Integer estado) {
        // Verificar que el estado sea válido
        if (estado == null || (estado != 1 && estado != 2)) {
            throw new EstadoNoValidoExcepction("El estado proporcionado no es válido.");
        }

        // Usar el repositorio para encontrar los alquileres con el estado dado
        List<Alquiler> alquileres = alquilerRepository.findByEstado(estado);

        // Devuelve la lista de alquileres filtrada por el estado
        return alquileres
                .stream()
                .map(alquilerResponseDtoMapper)
                .toList();
    }

    public List<AlquilerResponseDto> obtenerTodosLosAlquileres() {
        List<Alquiler> alquileres = alquilerRepository.findAll();
        return alquileres
                .stream()
                .map(alquilerResponseDtoMapper)
                .toList();

    }


}
