package com.microservicios.app.gateway.security;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

/**
 * Filtro CORS para Spring Cloud Gateway (WebFlux).
 *
 * Orden -2: se ejecuta ANTES que Spring Security (orden -1)
 * y antes que JwtAuthFilter (orden -1).
 *
 * Maneja:
 *   - Peticiones preflight OPTIONS → responde 200 inmediatamente
 *   - Todas las demas peticiones → añade headers CORS a la respuesta
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class CorsFilter implements WebFilter {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        ServerHttpRequest request  = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();
        HttpHeaders headers = response.getHeaders();

        // Añadir headers CORS a todas las respuestas
        String origin = request.getHeaders().getOrigin();
        if (origin != null) {
            headers.set("Access-Control-Allow-Origin", origin);
        } else {
            headers.set("Access-Control-Allow-Origin", "http://localhost:4200");
        }
        headers.set("Access-Control-Allow-Credentials", "true");
        headers.set("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, PATCH, HEAD");
        headers.set("Access-Control-Allow-Headers", "*");
        headers.set("Access-Control-Expose-Headers", "Authorization, Content-Type");
        headers.set("Access-Control-Max-Age", "3600");

        // Peticion preflight OPTIONS: responder 200 sin pasar al siguiente filtro
        if (HttpMethod.OPTIONS.equals(request.getMethod())) {
            response.setStatusCode(HttpStatus.OK);
            return response.setComplete();
        }

        return chain.filter(exchange);
    }
}
