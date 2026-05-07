package com.microservicios.app.respuestas.models.repository;

import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.microservicios.app.respuestas.models.entity.Respuesta;

import java.util.List;

public interface RespuestaRepository extends MongoRepository<Respuesta, String> {

    /** Respuestas de un alumno filtrando por lista de IDs de pregunta */
    @Query("{'alumnoId':?0, 'preguntaId': {$in: ?1} }")
    List<Respuesta> findRespuestaByAlumnoByPreguntaIds(Long alumnoId, Iterable<Long> preguntaIds);

    /** Todas las respuestas de un alumno */
    @Query("{'alumnoId':?0}")
    List<Respuesta> findByAlumnoId(Long alumnoId);

    /** Respuestas de un alumno en un examen concreto (todos los intentos) */
    @Query("{'alumnoId':?0,'pregunta.examen.id': ?1 }")
    List<Respuesta> findRespuestaByAlumnoByExamen(Long alumnoId, Long examenId);

    /**
     * Respuestas de un alumno en un examen para un intento concreto.
     * Permite recuperar exactamente las respuestas de la sesión N.
     */
    @Query("{'alumnoId':?0,'pregunta.examen.id': ?1, 'numeroIntento': ?2 }")
    List<Respuesta> findRespuestaByAlumnoByExamenByIntento(Long alumnoId, Long examenId, Integer numeroIntento);

    /**
     * Proyeccion para obtener solo el campo examen.id de las respuestas del alumno.
     * Usado para marcar qué exámenes ya tienen al menos un intento.
     */
    @Query(value="{'alumnoId':?0}", fields="{'pregunta.examen.id': 1}")
    List<Respuesta> findExamenesIdsConRespuestasByAlumno(Long alumnoId);

    /**
     * Número máximo de intento que ha hecho este alumno en este examen.
     * Equivalente a: SELECT MAX(numeroIntento) ... GROUP BY examen, alumno
     * Se implementa en el service con stream max().
     */
    @Query("{'alumnoId':?0,'pregunta.examen.id': ?1 }")
    List<Respuesta> findAllByAlumnoAndExamen(Long alumnoId, Long examenId);
}
