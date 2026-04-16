package com.microservicios.app.respuestas.models.services;

import com.microservicios.app.respuestas.models.entity.Respuesta;

public interface RespuestaService {

	public Iterable<Respuesta>  saveAll(Iterable<Respuesta> respuestas);
	
	public Iterable<Respuesta> findRespuestaByAlumnoByExamen(Long alumnoId, Long examenId);

	//busqueda en mongo
	public Iterable<Long> findExamenesIdsConRespuestasByAlumno(Long alumnoId);

	//busqueda en mongo
	public Iterable<Respuesta> findByAlumnoId(Long alumnoId);

}
