package com.example.ApiGateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtAuthenticationConverter;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.beans.factory.annotation.Value;

@Configuration
@EnableWebFluxSecurity
public class GWConfig {
    //localhost:8082/estaciones
    //REDIRECCION A MICROSERVICIOS.
    @Bean
    public RouteLocator configurarRutas(RouteLocatorBuilder builder,
                                        @Value("${estaciones.url-microservicio-estaciones}") String uriEstaciones,
                                        @Value("${alquileres.url-microservicio-alquileres}") String uriAlquileres) {
        return builder.routes()
                .route(p -> p.path("/estaciones/**").uri(uriEstaciones))
                .route(p -> p.path("/api/alquileres/**").uri(uriAlquileres))
                .build();
    }

    //SEGURIDAD, REGLAS DE AUTORIZACION PARA LAS RUTAS.
    @Bean
    public SecurityWebFilterChain filterChain(ServerHttpSecurity http) throws Exception {
        http.authorizeExchange(exchanges -> exchanges
                        .pathMatchers(HttpMethod.POST, "/estaciones").hasRole("ADMINISTRADOR")
                        .pathMatchers("/estaciones/**").hasRole("CLIENTE")
                        .pathMatchers(HttpMethod.POST, "/api/alquileres/iniciar", "/api/alquileres/finalizar").hasRole("CLIENTE")
                        .pathMatchers(HttpMethod.GET, "/api/alquileres/estado/*", "/api/alquileres").hasRole("ADMINISTRADOR")
                        .anyExchange().authenticated())
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults())) //Configuración OAuth 2.0 Resource Server
                .csrf(ServerHttpSecurity.CsrfSpec::disable);
        return http.build();
    }
//Manejar la conversión de tokens JWT  en objetos de autenticación de Spring Security
    @Bean
    public ReactiveJwtAuthenticationConverter jwtAuthenticationConverter() {
        var jwtAuthenticationConverter = new ReactiveJwtAuthenticationConverter();
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(new KeycloakReactiveJwtGrantedAuthoritiesConverter());
        return jwtAuthenticationConverter;
    }
}
