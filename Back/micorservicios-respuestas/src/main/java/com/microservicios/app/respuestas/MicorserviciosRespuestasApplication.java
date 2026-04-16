package com.microservicios.app.respuestas;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableAutoConfiguration(exclude = {DataSourceAutoConfiguration.class})
@EnableFeignClients
@EnableDiscoveryClient
@SpringBootApplication
//ya no se registran
//@EntityScan({"com.microservicios.app.respuestas.models.entity",
//	"com.microservicios.commons.alumnos.models.entity",
//	"com.microservicios.commons.examenesmodels.entity"})
public class MicorserviciosRespuestasApplication {

	public static void main(String[] args) {
		SpringApplication.run(MicorserviciosRespuestasApplication.class, args);
	}

}
