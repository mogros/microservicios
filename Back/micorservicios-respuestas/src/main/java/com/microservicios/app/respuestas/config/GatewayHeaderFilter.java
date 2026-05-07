package com.microservicios.app.respuestas.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Verifica que la peticion viene del Gateway (tiene header X-Auth-Username).
 * Si no tiene el header, rechaza con 401.
 */
public class GatewayHeaderFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(GatewayHeaderFilter.class);
    public static final String HEADER_USERNAME = "X-Auth-Username";
    public static final String HEADER_ROLES    = "X-Auth-Roles";

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String username = request.getHeader(HEADER_USERNAME);

        if (username == null || username.isBlank()) {
            log.warn("Peticion rechazada: sin header {} — acceso directo al microservicio", HEADER_USERNAME);
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\":\"Acceso directo al microservicio no permitido\"}");
            return;
        }

        List<SimpleGrantedAuthority> authorities = Collections.emptyList();
        String rolesHeader = request.getHeader(HEADER_ROLES);
        if (rolesHeader != null && !rolesHeader.isBlank()) {
            authorities = Arrays.stream(rolesHeader.split(","))
                    .map(String::trim)
                    .filter(r -> !r.isEmpty())
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toList());
        }

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(username, null, authorities));

        log.debug("Peticion autenticada via Gateway para: {}", username);
        filterChain.doFilter(request, response);
    }
}
