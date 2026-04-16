package com.microservicios.app.examenes.services;

import java.util.List;

import com.microservicios.commons.examenesmodels.entity.Asignatura;
import com.microservicios.commons.examenesmodels.entity.Examen;
import com.microservicios.commons.service.CommonService;

public interface ExamenService extends CommonService<Examen> {

	public List<Examen> findByNombre(String term);
	
	public Iterable<Asignatura> findAllAsignaturas();

	//traer examens con respuesta de las prguntas
	public Iterable<Long> findExamenesIdsConRespuestasByPreguntaIds(Iterable<Long> prguntaIds);

}
