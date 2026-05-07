package com.microservicios.app.auth.config;

import com.microservicios.app.auth.service.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Configuracion de seguridad de ms-auth.
 *
 * IMPORTANTE: /api/auth/** es completamente publica — no requiere ningun header.
 * La autenticacion ocurre DENTRO del AuthController cuando se llama a
 * authenticationManager.authenticate(), no a nivel de filtro HTTP.
 *
 * Por eso se deshabilitan todos los mecanismos de autenticacion HTTP
 * (httpBasic, formLogin) para que Spring Security no intente autenticar
 * la peticion antes de que llegue al controller.
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // Sin estado: no se crean sesiones HTTP
            .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            // CSRF deshabilitado: API REST sin sesion
            .csrf(AbstractHttpConfigurer::disable)
            // Sin login por formulario: evita que Spring Security intercepte /login
            .formLogin(AbstractHttpConfigurer::disable)
            // Sin HTTP Basic: evita que Spring Security pida credenciales en el header
            .httpBasic(AbstractHttpConfigurer::disable)
            // Todas las rutas permitidas a nivel de filtro HTTP.
            // La autenticacion real ocurre dentro del AuthController.
            .authorizeHttpRequests(auth -> auth
                .anyRequest().permitAll()
            )
            .authenticationProvider(authenticationProvider());

        return http.build();
    }
}
