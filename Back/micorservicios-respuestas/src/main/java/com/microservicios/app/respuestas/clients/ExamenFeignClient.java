package com.microservicios.app.respuestas.clients;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import com.microservicios.commons.examenesmodels.entity.Examen;

@FeignClient(name = "microservicios-examenes")  //del properties de examenes
public interface ExamenFeignClient {

	@GetMapping("/{id}")
	public Examen obtenerExamenPorId(@PathVariable Long id);
	
	@GetMapping("/respondidos-por-preguntas")
	public List<Long>  obtenerExamenesIdsPorPreguntasIdRespondidas(
			@RequestParam List<Long> prguntaIds);
}
