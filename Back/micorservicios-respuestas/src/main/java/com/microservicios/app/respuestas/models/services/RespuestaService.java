package com.microservicios.app.respuestas.models.services;

import com.microservicios.app.respuestas.models.entity.Respuesta;
import java.util.List;

public interface RespuestaService {

    /** Guarda un conjunto de respuestas (un intento completo) */
    List<Respuesta> saveAll(Iterable<Respuesta> respuestas);

    /** Todas las respuestas de un alumno en un examen (todos los intentos) */
    List<Respuesta> findRespuestaByAlumnoByExamen(Long alumnoId, Long examenId);

    /** Respuestas de un alumno en un examen para un intento concreto */
    List<Respuesta> findRespuestaByAlumnoByExamenByIntento(Long alumnoId, Long examenId, Integer numeroIntento);

    /** IDs de los exámenes que el alumno ha respondido al menos una vez */
    List<Long> findExamenesIdsConRespuestasByAlumno(Long alumnoId);

    /** Todas las respuestas de un alumno (todos los exámenes) */
    List<Respuesta> findByAlumnoId(Long alumnoId);

    /**
     * Calcula el siguiente número de intento para alumno + examen.
     * Si nunca ha respondido retorna 1. Si ya respondió N veces retorna N+1.
     */
    int calcularSiguienteIntento(Long alumnoId, Long examenId);

    /** Número máximo de intentos realizados en un examen */
    int contarIntentos(Long alumnoId, Long examenId);
}
