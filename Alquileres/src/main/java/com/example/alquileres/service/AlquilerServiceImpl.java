package com.example.alquileres.service;

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
    static final Logger logger = LoggerFactory.getLogger(AlquilerServiceImpl.class);
    @Transactional
    public void iniciarAlquiler(Integer idEstacion, String idCliente) {
        // Verificar que la estación existe
        var estacion = estacionesApiClient.getEstacionById(idEstacion);
        if (estacion == null) {
            throw new IllegalStateException("La estación con ID " + idEstacion + " no existe.");
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
            throw new IllegalStateException("No se encontró una tarifa adecuada para la fecha y hora actual.");
        }

        // Crear un nuevo objeto alquiler con los datos de inicio y empiezo a setterlo.
        Alquiler alquiler = new Alquiler();
        alquiler.setId(identifierRepository.nextValue(Alquiler.TABLE_NAME));
        alquiler.setIdCliente(idCliente);
        alquiler.setEstado(1); // Estado "Iniciado"
        alquiler.setEstacionRetiro(idEstacion);
        alquiler.setFechaHoraRetiro(java.time.LocalDateTime.now());
        alquiler.setTarifa(tarifa);
        // Los campos de devolución y monto se dejan nulos, se actualizarán al finalizar el alquiler.

        // Guardar el nuevo alquiler
        alquilerRepository.save(alquiler);
    }

    @Transactional
    public void finalizarAlquiler(Integer idAlquiler, Integer idEstacionDevolucion, String monedaDeseada) {
        logger.info("Iniciando el proceso de finalizar alquiler para el id: {}", idAlquiler);


        // Obtener alquiler
        Alquiler alquiler = alquilerRepository.findById(idAlquiler)
                .orElseThrow(() -> new IllegalStateException("Alquiler no encontrado"));

        logger.info("Alquiler obtenido para el id: {}", alquiler.getId());

        // Verificar estado del alquiler
        if (alquiler.getEstado() == 2) {
            throw new IllegalStateException("El alquiler ya ha sido finalizado.");
        }

        // Obtener estación de retiro y devolución
        var estacionRetiro = estacionesApiClient.getEstacionById(alquiler.getEstacionRetiro());
        if (estacionRetiro == null) {
            throw new IllegalStateException("La estación de retiro con ID " + alquiler.getEstacionRetiro() + " no existe.");
        }
        var estacionDevolucion = estacionesApiClient.getEstacionById(idEstacionDevolucion);
        if (estacionDevolucion == null) {
            throw new IllegalStateException("La estación de devolución con ID " + idEstacionDevolucion + " no existe.");
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
            throw new IllegalStateException("No se ha asignado tarifa al alquiler con ID " + alquiler.getId());
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
        if (monedaDeseada != null && !monedaDeseada.isEmpty()) {
            try {
                montoTotal = convertirMoneda(montoTotal, "ARS", monedaDeseada);
                logger.info("Monto convertido para el alquiler id: {} en moneda {}: {}", alquiler.getId(), monedaDeseada, montoTotal);
            } catch (IllegalStateException e) {
                logger.error("Error al convertir la moneda para el alquiler id: {}", alquiler.getId(), e);
                throw new IllegalStateException("No se pudo convertir la moneda: " + e.getMessage());
            }
        }

        // Actualizar alquiler
        alquiler.setEstado(2); // Estado "Finalizado"
        alquiler.setEstacionDevolucion(idEstacionDevolucion);
        alquiler.setFechaHoraDevolucion(LocalDateTime.now());
        alquiler.setMonto(montoTotal);

        // Guardar el alquiler actualizado
        alquilerRepository.save(alquiler);
        logger.info("Alquiler finalizado y guardado con id: {}", alquiler.getId());

    }

    public double calcularDistancia(double lat1, double lon1, double lat2, double lon2) {
        // Calcular distancia euclídea y convertir grados a metros
        double latitudDistancia = Math.toRadians(lat2 - lat1);
        double longitudDistancia = Math.toRadians(lon2 - lon1);

        return Math.sqrt(Math.pow(latitudDistancia, 2) + Math.pow(longitudDistancia, 2)) * 110000;
    }

    public double calcularMontoTotal(LocalDateTime inicio, LocalDateTime fin, Tarifa tarifa, double distancia) {
        if (inicio == null || fin == null) {
            throw new IllegalArgumentException("Las fechas de inicio y fin no pueden ser nulas");
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
                throw new IllegalStateException("Tasa de cambio no encontrada para la moneda: " + monedaDeseada);
            }
            double tasaDeCambio;
            if (tasaDeCambioObjeto instanceof Integer) {
                tasaDeCambio = ((Integer) tasaDeCambioObjeto).doubleValue();
            } else if (tasaDeCambioObjeto instanceof Double) {
                tasaDeCambio = (Double) tasaDeCambioObjeto;
            } else {
                throw new IllegalStateException("Formato de tasa de cambio no esperado para la moneda: " + monedaDeseada);
            }
            return monto * tasaDeCambio;
        } catch (FeignException e) {
            logger.error("El servicio de conversión de moneda no está disponible en este momento", e);
            throw new IllegalStateException("El servicio de conversión de moneda no está disponible en este momento", e);
        } catch (Exception e) {
            logger.error("Ocurrió un error al realizar la conversión de moneda", e);
            throw new IllegalStateException("Ocurrió un error al realizar la conversión de moneda", e);
        }
    }


    public List<Alquiler> obtenerAlquileresPorEstado(Integer estado) {
        // Verificar que el estado sea válido
        if (estado == null || (estado != 1 && estado != 2)) {
            throw new IllegalArgumentException("El estado proporcionado no es válido.");
        }

        // Usar el repositorio para encontrar los alquileres con el estado dado
        List<Alquiler> alquileres = alquilerRepository.findByEstado(estado);

        // Devuelve la lista de alquileres filtrada por el estado
        return alquileres;
    }

    public List<Alquiler> obtenerTodosLosAlquileres() {
        return alquilerRepository.findAll();
    }


}
