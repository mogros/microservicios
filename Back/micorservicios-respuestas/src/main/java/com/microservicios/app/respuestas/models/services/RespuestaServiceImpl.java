package com.microservicios.app.respuestas.models.services;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.microservicios.app.respuestas.clients.ExamenFeignClient;
import com.microservicios.app.respuestas.models.entity.Respuesta;
import com.microservicios.app.respuestas.models.repository.RespuestaRepository;
import com.microservicios.commons.examenesmodels.entity.Examen;
import com.microservicios.commons.examenesmodels.entity.Pregunta;

@Service
public class RespuestaServiceImpl implements RespuestaService {

	@Autowired
	private RespuestaRepository repository;
	
	//jalar de examen 
	@Autowired
	private ExamenFeignClient examenClient;
	
	
	@Override
	//@Transactional		mongodb nno es transaccional
	public Iterable<Respuesta> saveAll(Iterable<Respuesta> respuestas) {
		
		return repository.saveAll(respuestas);
	}

	@Override
	//@Transactional(readOnly = true)		mongodb nno es transaccional
	public Iterable<Respuesta> findRespuestaByAlumnoByExamen(Long alumnoId, Long examenId) {
		//return repository.findRespuestaByAlumnoByExamen(alumnoId, examenId);
		
		/* ahora la consulta e slocal ya no aplicas esto
		Examen examen = examenClient.obtenerExamenPorId(examenId);
		List<Pregunta> preguntas = examen.getPreguntas();
		List<Long> preguntasIds = preguntas.stream().map(p -> p.getId()).collect(Collectors.toList());
		List<Respuesta> respuestas=(List<Respuesta>) repository.findRespuestaByAlumnoByPreguntaIds(alumnoId, preguntasIds);
		respuestas=respuestas.stream().map(r->{
			preguntas.forEach(p->{
				if(p.getId()==r.getPreguntaId()) {
					r.setPregunta(p);
				}
			});
			return r;
		}).collect(Collectors.toList()) ;
		*/
		
		//consulta local
		List<Respuesta> respuestas=(List<Respuesta>) repository.findRespuestaByAlumnoByExamen(alumnoId, examenId);

		
		return respuestas;
	}

	@Override
	//@Transactional(readOnly = true)		mongodb nno es transaccional
	public Iterable<Long> findExamenesIdsConRespuestasByAlumno(Long alumnoId) {
		
		/*
		//busacr respeustas de alumno en mongo
		List<Respuesta> respuestasAlumno= (List<Respuesta>) repository.findByAlumnoId(alumnoId);
		List<Long> examenIds = Collections.emptyList();
		if(respuestasAlumno.size()>0) {
			List<Long> preguntaIds= respuestasAlumno.stream().map(r-> r.getPreguntaId())
					.collect(Collectors.toList());
			examenIds = examenClient.obtenerExamenesIdsPorPreguntasIdRespondidas(preguntaIds);
		}
		*/
		
		List<Respuesta> respuestasAlumno= (List<Respuesta>) repository.findExamenesIdsConRespuestasByAlumno(alumnoId);
		List<Long> examenIds = respuestasAlumno.
				stream()
				.map(r-> r.getPregunta().getExamen().getId())
				.distinct()
				.collect(Collectors.toList());
		return examenIds;
	}

	@Override
	public Iterable<Respuesta> findByAlumnoId(Long alumnoId) {
		// TODO Auto-generated method stub
		return repository.findByAlumnoId(alumnoId);
	}

	
	
}
