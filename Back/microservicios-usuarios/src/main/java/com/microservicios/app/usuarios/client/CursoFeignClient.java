package com.microservicios.app.usuarios.client;

import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;

//antes agrega al  pom y al principal @EnableFeignClients
//niombre de US con el q te vas a comunicar del properties
@FeignClient(name="microservicios-cursos")			
public interface CursoFeignClient {

	@DeleteMapping("/eliminar-alumno/{id}")
	public void eliminarCursoAlumnoPorId(@PathVariable Long id);
}
