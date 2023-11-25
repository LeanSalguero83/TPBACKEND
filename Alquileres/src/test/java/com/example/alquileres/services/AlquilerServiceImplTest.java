package com.example.alquileres.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.example.alquileres.model.Alquiler;

import java.time.Month;
import java.util.*;
import java.time.LocalDateTime;


import com.example.alquileres.model.Estacion;
import com.example.alquileres.model.Tarifa;
import com.example.alquileres.repository.*;
import com.example.alquileres.service.AlquilerServiceImpl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.internal.matchers.Null;
import org.mockito.junit.jupiter.MockitoExtension;


public class AlquilerServiceImplTest {

    @Mock
    private AlquilerRepository alquilerRepository;
    @Mock
    private IdentifierRepository identifierRepository;
    @Mock
    private IEstacionesApiClient estacionesApiClient;
    @Mock
    private TarifaRepository tarifaRepository;
    @Mock
    private ExchangeRateApiClient exchangeRateApiClient;

    @InjectMocks
    private AlquilerServiceImpl alquilerService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }


    @Test
    public void testCalcularDistancia_Caso1() {
        double distancia = alquilerService.calcularDistancia(-31.442961, -64.194091, -31.439612, -64.189333);
        assertTrue(distancia > 0, "La distancia debería ser mayor que cero.");
    }

    @Test
    public void testCalcularDistancia_Caso2() {
        double distancia = alquilerService.calcularDistancia(-31.442961, -64.194091, -31.442961, -64.194091);
        assertEquals(0, distancia, "La distancia debería ser cero para la misma ubicación.");
    }

    @Test
    public void testCalcularDistancia_Caso3() {
        double distancia = alquilerService.calcularDistancia(-31.442961, -64.194091, -31.450822, -64.200468);
        assertTrue(distancia > 0, "La distancia debería ser mayor que cero.");
    }

    @Test
    public void testCalcularDistancia_Caso4() {

        double latitud1 = -31.442961;
        double longitud1 = -64.194091;
        double latitud2 = -31.368295;
        double longitud2 = -64.245599;

        double distancia = alquilerService.calcularDistancia(latitud1, longitud1, latitud2, longitud2);
        assertTrue(distancia > 0, "La distancia debería ser mayor que cero para puntos distantes.");
    }

    @Test
    public void obtenerTodosLosAlquileresTest() {
        Tarifa tarifaMock = new Tarifa();
        tarifaMock.setId(1);
        Alquiler alquiler1 = new Alquiler();
        alquiler1.setId(1);
        alquiler1.setIdCliente("1");
        alquiler1.setEstado(2);
        alquiler1.setEstacionRetiro(1);
        alquiler1.setEstacionDevolucion(2);
        alquiler1.setFechaHoraRetiro(LocalDateTime.of(2023, 11, 5, 15, 51, 41));
        alquiler1.setFechaHoraDevolucion(LocalDateTime.of(2023, 11, 5, 16, 5, 29));
        alquiler1.setMonto(378.89);
        alquiler1.setTarifa(tarifaMock);


        Alquiler alquiler2 = new Alquiler();
        alquiler2.setId(2);
        alquiler2.setIdCliente("1");
        alquiler2.setEstado(2);
        alquiler2.setEstacionRetiro(1);
        alquiler2.setEstacionDevolucion(2);
        alquiler2.setFechaHoraRetiro(LocalDateTime.of(2023, 11, 5, 16, 12, 35));
        alquiler2.setFechaHoraDevolucion(LocalDateTime.of(2023, 11, 5, 16, 15, 28));
        alquiler2.setMonto(0.89);
        alquiler2.setTarifa(tarifaMock);

        List<Alquiler> alquileresMock = Arrays.asList(alquiler1, alquiler2);

        when(alquilerRepository.findAll()).thenReturn(alquileresMock);

        List<Alquiler> alquileresObtenidos = alquilerService.obtenerTodosLosAlquileres();

        assertNotNull(alquileresObtenidos, "La lista de alquileres no debe ser nula.");
        assertEquals(2, alquileresObtenidos.size(), "La lista de alquileres debe contener dos elementos.");

        assertEquals(alquiler1.getId(), alquileresObtenidos.get(0).getId());
        assertEquals(alquiler1.getMonto(), alquileresObtenidos.get(0).getMonto());
        assertEquals(alquiler2.getId(), alquileresObtenidos.get(1).getId());
        assertEquals(alquiler2.getMonto(), alquileresObtenidos.get(1).getMonto());

        assertTrue(alquileresObtenidos.containsAll(alquileresMock), "La lista de alquileres debe contener los elementos esperados.");
    }

    @Test
    public void obtenerAlquileresPorEstado_CuandoEstadoEsValido_DeberiaRetornarListaDeAlquileres() {
        Alquiler alquiler1 = new Alquiler();
        alquiler1.setEstado(1);
        Alquiler alquiler2 = new Alquiler();
        alquiler2.setEstado(1);

        when(alquilerRepository.findByEstado(1)).thenReturn(Arrays.asList(alquiler1, alquiler2));


        List<Alquiler> resultado = alquilerService.obtenerAlquileresPorEstado(1);

        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        assertTrue(resultado.contains(alquiler1));
        assertTrue(resultado.contains(alquiler2));
    }

    @Test
    public void obtenerAlquileresPorEstado_CuandoEstadoNoEsValido_DeberiaLanzarExcepcion() {
        assertThrows(IllegalArgumentException.class, () -> {
            alquilerService.obtenerAlquileresPorEstado(3);
        });
    }

    @Test
    public void obtenerAlquileresPorEstado_CuandoNoHayAlquileresConEseEstado_DeberiaRetornarListaVacia() {
        // Preparación
        when(alquilerRepository.findByEstado(1)).thenReturn(List.of());

        // Acción
        List<Alquiler> resultado = alquilerService.obtenerAlquileresPorEstado(1);

        // Verificación
        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
    }

    @Test
    public void calcularMontoTotal_CasoTarifaNormal() {
        // Configuración del entorno de prueba
        LocalDateTime inicio = LocalDateTime.of(2023, Month.NOVEMBER, 5, 15, 30);
        LocalDateTime fin = LocalDateTime.of(2023, Month.NOVEMBER, 5, 16, 30);
        Tarifa tarifa = new Tarifa();
        tarifa.setMontoFijoAlquiler(300.0);
        tarifa.setMontoHora(240.0);
        tarifa.setMontoMinutoFraccion(6.0);
        tarifa.setMontoKm(80.0);

        double distancia = 1000; // 1 km

//        when(tarifaRepository.findById(1)).thenReturn(Optional.of(tarifa));

        // Llamada al método a probar
        double montoTotal = alquilerService.calcularMontoTotal(inicio, fin, tarifa, distancia);

        // Verificación
        double montoEsperado = 300.0 + 240.0 + 80.0; // Tarifa fija + 1 hora + 1 km
        assertEquals(montoEsperado, montoTotal, "El monto total calculado debería ser el esperado para una tarifa normal.");
    }

    @Test
    public void calcularMontoTotal_CasoTarifaEspecial() {
        // Configuración del entorno de prueba para una tarifa especial
        LocalDateTime inicio = LocalDateTime.of(2023, Month.OCTOBER, 13, 10, 0);
        LocalDateTime fin = LocalDateTime.of(2023, Month.OCTOBER, 13, 11, 30);
        Tarifa tarifa = new Tarifa();
        tarifa.setMontoFijoAlquiler(200.0);
        tarifa.setMontoHora(175.0);
        tarifa.setMontoMinutoFraccion(4.0);
        tarifa.setMontoKm(75.0);

        double distancia = 5000; // 5 km


//        when(tarifaRepository.findById(8)).thenReturn(Optional.of(tarifa));

        // Llamada al método a probar
        double montoTotal = alquilerService.calcularMontoTotal(inicio, fin, tarifa, distancia);

        // Verificación
        double montoEsperado = 200.0 + (1 * 175.0) + (30 * 4.0) + (5 * 75.0); // Tarifa fija + 1 hora + 30 minutos + 5 km
        assertEquals(montoEsperado, montoTotal, "El monto total calculado debería ser el esperado para una tarifa especial.");
    }

    @Test
    public void testConvertirMoneda_CuandoLaConversionEsExitosa() {
        double monto = 100.0;
        String monedaDeseada = "USD";
        Map<String, Object> tasaDeCambioMock = Map.of(
                "rates", Map.of(
                        "USD", 0.01
                )
        );

        when(exchangeRateApiClient.getExchangeRate("ARS")).thenReturn(tasaDeCambioMock);

        double montoConvertido = alquilerService.convertirMoneda(monto, "ARS", monedaDeseada);

        assertEquals(1.0, montoConvertido, "El monto convertido debe ser el esperado.");
    }

    @Test
    public void testConvertirMoneda_CuandoLaMonedaNoExiste() {
        double monto = 100.0;
        String monedaDeseada = "XXX";
        Map<String, Object> tasaDeCambioMock = Map.of(
                "rates", Map.of()
        );

        when(exchangeRateApiClient.getExchangeRate("ARS")).thenReturn(tasaDeCambioMock);

        assertThrows(IllegalStateException.class, () -> {
            alquilerService.convertirMoneda(monto, "ARS", monedaDeseada);
        }, "Debería lanzar una excepción cuando la moneda deseada no existe.");
    }

    @Test
    public void testConvertirMoneda_CuandoOcurreUnErrorEnLaAPI() {
        double monto = 100.0;
        String monedaDeseada = "USD";

        when(exchangeRateApiClient.getExchangeRate("ARS")).thenThrow(new RuntimeException("Error de API"));

        assertThrows(IllegalStateException.class, () -> {
            alquilerService.convertirMoneda(monto, "ARS", monedaDeseada);
        }, "Debería lanzar una excepción cuando ocurre un error en la API.");
    }

    // 1. Prueba para verificar la existencia de la estación
    @Test
    public void iniciarAlquiler_EstacionNoExiste_LanzaExcepcion() {
        // Configuración del entorno de prueba
        when(estacionesApiClient.getEstacionById(anyInt())).thenReturn(null);

        // Ejecutar y verificar
        assertThrows(IllegalStateException.class, () -> {
            alquilerService.iniciarAlquiler(99, "cliente1");
        });
    }

    @Test
    public void iniciarAlquiler_TarifaNoExiste_LanzaExcepcion() {
        // Configuración del entorno de prueba
        when(estacionesApiClient.getEstacionById(anyInt())).thenReturn(new Estacion());
        when(tarifaRepository.encontrarTarifasPorFecha(anyInt(), anyInt(), anyInt(), anyInt())).thenReturn(Collections.emptyList());

        // Ejecutar y verificar
        assertThrows(IllegalStateException.class, () -> {
            alquilerService.iniciarAlquiler(1, "cliente1");
        });
    }
    @Test
    public void iniciarAlquiler_ConDatosValidos_CreaAlquiler() {
        // Configuración de datos de prueba
        Integer idEstacion = 1;
        String idCliente = "cliente123";

        // Mocking y expectativas
        when(estacionesApiClient.getEstacionById(idEstacion)).thenReturn(new Estacion());
        when(tarifaRepository.encontrarTarifasPorFecha(anyInt(), anyInt(), anyInt(), anyInt())).thenReturn(List.of(new Tarifa()));
        when(identifierRepository.nextValue(Alquiler.TABLE_NAME)).thenReturn(1);

        // Ejecución del método a probar
        alquilerService.iniciarAlquiler(idEstacion, idCliente);

        // Verificaciones
        ArgumentCaptor<Alquiler> alquilerCaptor = ArgumentCaptor.forClass(Alquiler.class);
        verify(alquilerRepository).save(alquilerCaptor.capture());
        Alquiler alquilerGuardado = alquilerCaptor.getValue();

        assertNotNull(alquilerGuardado, "El alquiler no debe ser nulo.");
        assertEquals(idCliente, alquilerGuardado.getIdCliente(), "El ID del cliente debe coincidir.");
        assertEquals(idEstacion, alquilerGuardado.getEstacionRetiro(), "La estación de retiro debe coincidir.");
    }


    @Test
    public void finalizarAlquiler_AlquilerNoEncontrado_LanzaExcepcion() {
        when(alquilerRepository.findById(anyInt())).thenReturn(Optional.empty());
        assertThrows(IllegalStateException.class, () -> alquilerService.finalizarAlquiler(1, 2, "ARS"));
    }

    @Test
    public void finalizarAlquiler_AlquilerYaFinalizado_LanzaExcepcion() {
        Alquiler alquiler = mock(Alquiler.class);
        when(alquiler.getEstado()).thenReturn(2);
        when(alquilerRepository.findById(anyInt())).thenReturn(Optional.of(alquiler));

        assertThrows(IllegalStateException.class, () -> alquilerService.finalizarAlquiler(1, 2, "ARS"));
    }

    @Test
    public void finalizarAlquiler_EstacionDevolucionNoEncontrada_LanzaExcepcion() {
        Alquiler alquiler = mock(Alquiler.class);
        when(alquiler.getEstado()).thenReturn(1);
        when(alquilerRepository.findById(anyInt())).thenReturn(Optional.of(alquiler));
        when(estacionesApiClient.getEstacionById(anyInt())).thenReturn(null);

        assertThrows(IllegalStateException.class, () -> alquilerService.finalizarAlquiler(1, 99, "ARS"));
    }

    @Test
    public void finalizarAlquiler_TarifaNoEncontrada_LanzaExcepcion() {
        Alquiler alquiler = mock(Alquiler.class);
        when(alquiler.getEstado()).thenReturn(1);
        when(alquilerRepository.findById(anyInt())).thenReturn(Optional.of(alquiler));
        Estacion estacion = mock(Estacion.class);
        when(estacionesApiClient.getEstacionById(anyInt())).thenReturn(estacion);
        when(tarifaRepository.findById(anyInt())).thenReturn(Optional.empty());

        assertThrows(IllegalStateException.class, () -> alquilerService.finalizarAlquiler(1, 2, "ARS"));
    }

    @Test
    public void finalizarAlquiler_ConversionMonedaFalla_LanzaExcepcion() {
        // Mock del objeto Alquiler y configuración de sus métodos
        Alquiler alquiler = mock(Alquiler.class);
        when(alquiler.getEstado()).thenReturn(1);
        when(alquiler.getFechaHoraRetiro()).thenReturn(LocalDateTime.now());
        when(alquiler.getFechaHoraDevolucion()).thenReturn(LocalDateTime.now().plusHours(2)); // Asegúrate de que esta fecha sea posterior a la fecha de retiro
        when(alquilerRepository.findById(anyInt())).thenReturn(Optional.of(alquiler));

        // Configuración de otros mocks
        Estacion estacion = mock(Estacion.class);
        when(estacionesApiClient.getEstacionById(anyInt())).thenReturn(estacion);
        Tarifa tarifaMock = mock(Tarifa.class);
        when(alquiler.getTarifa()).thenReturn(tarifaMock); // Asigna la tarifa mock al alquiler
        when(exchangeRateApiClient.getExchangeRate(anyString())).thenThrow(new RuntimeException("Error de API"));

        // Prueba
        assertThrows(IllegalStateException.class, () -> alquilerService.finalizarAlquiler(1, 2, "USD"));
    }

    @Test
    public void finalizarAlquiler_Exito_ActualizaAlquiler() {
        // Mock del objeto Alquiler y configuración de sus métodos
        Alquiler alquiler = mock(Alquiler.class);
        when(alquiler.getEstado()).thenReturn(1);
        when(alquiler.getFechaHoraRetiro()).thenReturn(LocalDateTime.now());
        when(alquiler.getFechaHoraDevolucion()).thenReturn(LocalDateTime.now().plusHours(2));
        when(alquilerRepository.findById(anyInt())).thenReturn(Optional.of(alquiler));

        // Configuración de otros mocks
        Estacion estacion = mock(Estacion.class);
        when(estacionesApiClient.getEstacionById(anyInt())).thenReturn(estacion);
        // Crear y configurar la tarifa mock
        Tarifa tarifaMock = mock(Tarifa.class);
        when(alquiler.getTarifa()).thenReturn(tarifaMock); // Asigna la tarifa mock al alquiler

        // Simulación de la respuesta del servicio de tasas de cambio
        Map<String, Object> fakeExchangeRateResponse = Map.of(
                "rates", Map.of(
                        "ARS", 1.0,  // Simulación de la tasa de cambio para Peso Argentino
                        "USD", 0.01  // Simulación de otra tasa de cambio
                )
        );
        when(exchangeRateApiClient.getExchangeRate(anyString())).thenReturn(fakeExchangeRateResponse);

        // Llamada al método bajo prueba
        alquilerService.finalizarAlquiler(1, 2, "USD");

        // Verificar actualización de alquiler
        verify(alquiler).setEstado(2);
        verify(alquiler).setEstacionDevolucion(2);
        verify(alquiler).setFechaHoraDevolucion(any(LocalDateTime.class));
        verify(alquiler).setMonto(anyDouble());

        // Verificar que se guardó el alquiler actualizado
        verify(alquilerRepository).save(alquiler);
    }


}

