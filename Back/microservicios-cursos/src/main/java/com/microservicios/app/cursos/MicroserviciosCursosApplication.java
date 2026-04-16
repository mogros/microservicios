package com.microservicios.app.cursos;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;


//@EnableEurekaClient
@EnableFeignClients
@EnableDiscoveryClient
@SpringBootApplication
@EntityScan({"com.microservicios.commons.alumnos.models.entity",
	"com.microservicios.app.cursos.models.entity",
	"com.microservicios.commons.examenesmodels.entity"})
public class MicroserviciosCursosApplication {

	public static void main(String[] args) {
		SpringApplication.run(MicroserviciosCursosApplication.class, args);
	}

}
