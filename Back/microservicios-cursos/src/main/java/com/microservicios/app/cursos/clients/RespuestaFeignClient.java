package com.microservicios.app.cursos.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

//nombre de US con el que te conectaas dl properties
@FeignClient(name="micorservicios-respuestas")
public interface RespuestaFeignClient {
	
	//ruta del controler
	@GetMapping("/alumno/{alumnoId}/examenes-respondidos")
	public Iterable<Long> obtenerExamenesIdConRespuestasAlumno(@PathVariable Long alumnoId);
	
}
