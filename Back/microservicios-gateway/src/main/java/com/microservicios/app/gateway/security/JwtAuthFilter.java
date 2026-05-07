package com.microservicios.app.gateway.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

/**
 * Filtro global JWT para Spring Cloud Gateway (WebFlux reactivo).
 *
 * Flujo por cada peticion:
 *   1. ¿Es ruta publica?          → dejar pasar sin validar
 *   2. ¿Tiene header Authorization? → si no, 401
 *   3. ¿Token valido y no expirado? → si no, 401
 *   4. Añadir X-Auth-Username al request y continuar
 *
 * NOTA sobre public-paths:
 *   @Value con List<String> no funciona con listas YAML en todos los contextos
 *   de Spring Cloud Gateway. Se lee como String CSV y se parsea aqui.
 *   Formato en application.yml: public-paths: /api/auth/,/actuator/health
 */
@Component
public class JwtAuthFilter implements GlobalFilter, Ordered {

    private static final Logger log = LoggerFactory.getLogger(JwtAuthFilter.class);

    @Value("${app.jwt.secret}")
    private String jwtSecret;

    /**
     * Se lee como String unico separado por comas y se convierte a lista en init().
     * Ejemplo: "/api/auth/,/actuator/health"
     */
    @Value("${app.security.public-paths}")
    private String publicPathsRaw;

    private List<String> publicPaths;

    /**
     * Inicializa la lista de rutas publicas a partir del String CSV.
     * Se llama automaticamente despues de la inyeccion de dependencias.
     */
    @jakarta.annotation.PostConstruct
    public void init() {
        publicPaths = Arrays.stream(publicPathsRaw.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .toList();
        log.info("Rutas publicas configuradas: {}", publicPaths);
    }

    @Override
    public int getOrder() {
        // Orden -1: se ejecuta antes que cualquier filtro del Gateway
        return -1;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String path = exchange.getRequest().getURI().getPath();

        // Paso 1: rutas publicas pasan sin validacion
        if (isPublicPath(path)) {
            return chain.filter(exchange);
        }

        // Paso 2: extraer token del header Authorization: Bearer <token>
        String token = extractToken(exchange.getRequest());
        if (token == null) {
            log.warn("Peticion rechazada (sin token): {}", path);
            return unauthorized(exchange, "Token no proporcionado");
        }

        // Paso 3: validar firma y expiracion
        Claims claims;
        try {
            claims = parseToken(token);
        } catch (ExpiredJwtException e) {
            log.warn("Token expirado en ruta: {}", path);
            return unauthorized(exchange, "Token expirado");
        } catch (JwtException e) {
            log.warn("Token invalido en ruta {}: {}", path, e.getMessage());
            return unauthorized(exchange, "Token invalido");
        }

        // Paso 4: propagar identidad al microservicio via header interno
        String username = claims.getSubject();
        ServerHttpRequest mutatedRequest = exchange.getRequest().mutate()
                .header("X-Auth-Username", username)
                .build();

        log.debug("JWT valido — usuario: '{}', ruta: {}", username, path);
        return chain.filter(exchange.mutate().request(mutatedRequest).build());
    }

    private boolean isPublicPath(String path) {
        return publicPaths.stream().anyMatch(path::startsWith);
    }

    private String extractToken(ServerHttpRequest request) {
        String header = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (header != null && header.startsWith("Bearer ")) {
            return header.substring(7);
        }
        return null;
    }

    private Claims parseToken(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
    }

    private Mono<Void> unauthorized(ServerWebExchange exchange, String message) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
        String body = "{\"error\":\"No autorizado\",\"message\":\"" + message + "\"}";
        DataBuffer buffer = response.bufferFactory()
                .wrap(body.getBytes(StandardCharsets.UTF_8));
        return response.writeWith(Mono.just(buffer));
    }
}
