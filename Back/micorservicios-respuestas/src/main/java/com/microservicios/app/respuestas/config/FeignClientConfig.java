package com.microservicios.app.respuestas.config;

import feign.RequestInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Configuration
public class FeignClientConfig {

    @Bean
    public RequestInterceptor propagarHeaderAutenticacion() {
        return requestTemplate -> {
            ServletRequestAttributes attrs =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attrs != null) {
                String username = attrs.getRequest().getHeader("X-Auth-Username");
                if (username != null && !username.isBlank()) {
                    requestTemplate.header("X-Auth-Username", username);
                }
            }
        };
    }
}
