package com.microservicios.app.cursos.config;

import feign.RequestInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * Configuracion global de Feign para ms-cursos.
 *
 * Cuando ms-cursos llama a ms-usuarios o ms-respuestas via Feign,
 * los microservicios destino tienen GatewayHeaderFilter que exige
 * el header X-Auth-Username. Este interceptor lo propaga
 * automaticamente desde la request entrante al Feign saliente.
 */
@Configuration
public class FeignClientConfig {

    @Bean
    public RequestInterceptor propagarHeaderAutenticacion() {
        return requestTemplate -> {
            // Obtener la request HTTP actual (la que llego al ms-cursos)
            ServletRequestAttributes attrs =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

            if (attrs != null) {
                String username = attrs.getRequest().getHeader("X-Auth-Username");
                if (username != null && !username.isBlank()) {
                    // Propagar el header al microservicio destino
                    requestTemplate.header("X-Auth-Username", username);
                }
            }
        };
    }
}
