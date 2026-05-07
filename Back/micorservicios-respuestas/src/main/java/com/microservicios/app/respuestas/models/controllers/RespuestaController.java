package com.microservicios.app.respuestas.models.controllers;

import com.microservicios.app.respuestas.models.entity.Respuesta;
import com.microservicios.app.respuestas.models.services.RespuestaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
public class RespuestaController {

    private static final Logger log = LoggerFactory.getLogger(RespuestaController.class);

    @Autowired
    private RespuestaService service;

    /**
     * Guarda un intento completo de examen.
     *
     * El controller:
     *   1. Calcula el número de intento (MAX anterior + 1)
     *   2. Asigna la fecha y hora actual a todas las respuestas
     *   3. Desnormaliza alumnoId y preguntaId como campos planos
     *
     * El número de intento se calcula aquí (server-side) para evitar
     * que el cliente envíe cualquier valor arbitrario.
     */
    @PostMapping
    public ResponseEntity<?> crear(@RequestBody List<Respuesta> respuestas) {
        if (respuestas == null || respuestas.isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "La lista de respuestas no puede estar vacía"));
        }

        // Obtener alumno y examen de la primera respuesta para calcular el intento
        Respuesta primera = respuestas.get(0);
        Long alumnoId = primera.getAlumno() != null ? primera.getAlumno().getId() : null;
        Long examenId = primera.getPregunta() != null
                && primera.getPregunta().getExamen() != null
                        ? primera.getPregunta().getExamen().getId() : null;

        if (alumnoId == null || examenId == null) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Faltan datos de alumno o examen"));
        }

        int siguienteIntento = service.calcularSiguienteIntento(alumnoId, examenId);
        LocalDateTime ahora = LocalDateTime.now();

        log.info("Guardando intento {} del alumno {} en examen {}",
                siguienteIntento, alumnoId, examenId);

        List<Respuesta> procesadas = respuestas.stream().map(r -> {
            r.setAlumnoId(r.getAlumno().getId());
            r.setPreguntaId(r.getPregunta().getId());
            r.setNumeroIntento(siguienteIntento);
            r.setFechaRespuesta(ahora);
            return r;
        }).collect(Collectors.toList());

        List<Respuesta> guardadas = service.saveAll(procesadas);
        return ResponseEntity.status(HttpStatus.CREATED).body(guardadas);
    }

    /**
     * Devuelve todas las respuestas de un alumno en un examen (todos los intentos).
     * Útil para ver la evolución entre intentos.
     */
    @GetMapping("/alumno/{alumnoId}/examen/{examenId}")
    public ResponseEntity<?> obtenerRespuestasPorAlumnoPorExamen(
            @PathVariable Long alumnoId,
            @PathVariable Long examenId) {
        List<Respuesta> respuestas = service.findRespuestaByAlumnoByExamen(alumnoId, examenId);
        return ResponseEntity.ok(respuestas);
    }

    /**
     * Devuelve las respuestas de un intento concreto.
     * Permite ver exactamente qué respondió el alumno en el intento N.
     */
    @GetMapping("/alumno/{alumnoId}/examen/{examenId}/intento/{numeroIntento}")
    public ResponseEntity<?> obtenerRespuestasPorIntento(
            @PathVariable Long alumnoId,
            @PathVariable Long examenId,
            @PathVariable Integer numeroIntento) {
        List<Respuesta> respuestas = service.findRespuestaByAlumnoByExamenByIntento(
                alumnoId, examenId, numeroIntento);
        return ResponseEntity.ok(respuestas);
    }

    /**
     * Devuelve el número de intentos que ha realizado un alumno en un examen.
     * El frontend lo usa para saber si mostrar "Reintentar" y cuántas veces lo ha hecho.
     */
    @GetMapping("/alumno/{alumnoId}/examen/{examenId}/intentos")
    public ResponseEntity<?> contarIntentos(
            @PathVariable Long alumnoId,
            @PathVariable Long examenId) {
        int intentos = service.contarIntentos(alumnoId, examenId);
        return ResponseEntity.ok(Map.of(
                "alumnoId", alumnoId,
                "examenId", examenId,
                "totalIntentos", intentos,
                "siguienteIntento", intentos + 1
        ));
    }

    /**
     * IDs de exámenes que el alumno ha respondido al menos una vez.
     * Usado por ms-cursos para marcar respondido=true en la lista de exámenes.
     */
    @GetMapping("/alumno/{alumnoId}/examenes-respondidos")
    public ResponseEntity<?> obtenerExamenesIdConRespuestasAlumno(
            @PathVariable Long alumnoId) {
        List<Long> examenesIds = service.findExamenesIdsConRespuestasByAlumno(alumnoId);
        return ResponseEntity.ok(examenesIds);
    }
}
