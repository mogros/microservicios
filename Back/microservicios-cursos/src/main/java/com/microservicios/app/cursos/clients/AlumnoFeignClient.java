package com.microservicios.app.cursos.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.microservicios.commons.alumnos.models.entity.Alumno;

@FeignClient(name ="microservicios-usuarios")  ///dl properties usuarios
public interface AlumnoFeignClient {

	@GetMapping("/alumnos-por-curso")
	public Iterable<Alumno> obtenerAlumnosPorCurso(@RequestParam Iterable<Long> ids);
}
