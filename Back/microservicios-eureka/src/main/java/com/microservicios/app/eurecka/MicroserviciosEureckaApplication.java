package com.microservicios.app.eurecka;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@EnableEurekaServer
@SpringBootApplication
public class MicroserviciosEureckaApplication {

	public static void main(String[] args) {
		SpringApplication.run(MicroserviciosEureckaApplication.class, args);
	}

}
