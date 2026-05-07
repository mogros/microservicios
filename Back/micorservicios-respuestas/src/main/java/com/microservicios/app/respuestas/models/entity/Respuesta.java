package com.microservicios.app.respuestas.models.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.Indexed;

import com.microservicios.commons.alumnos.models.entity.Alumno;
import com.microservicios.commons.examenesmodels.entity.Pregunta;

import java.time.LocalDateTime;

/**
 * Documento MongoDB que representa la respuesta de un alumno
 * a una pregunta, en un intento concreto de un examen.
 *
 * Campos clave para historial de intentos:
 *   - numeroIntento: permite múltiples intentos del mismo examen
 *   - fechaRespuesta: cuándo se respondió
 *
 * alumnoId + preguntaId se duplican como campos planos para
 * facilitar las queries de MongoDB (evitan desestructurar objetos anidados).
 */
@Document(collection = "respuestas")
public class Respuesta {

    @Id
    private String id;

    private String texto;

    /** Objeto completo embebido (para mostrar en la UI sin llamada adicional) */
    private Alumno alumno;

    /** ID plano para queries eficientes en Mongo */
    @Indexed
    private Long alumnoId;

    /** Pregunta completa embebida (incluye referencia al examen) */
    private Pregunta pregunta;

    /** ID plano para queries eficientes en Mongo */
    @Indexed
    private Long preguntaId;

    /**
     * Número de intento del examen (1, 2, 3…).
     * El frontend envía este valor; el backend lo persiste tal cual.
     * Para calcular el siguiente intento: MAX(numeroIntento) + 1
     * consultando respuestas previas del mismo alumno y examen.
     */
    private Integer numeroIntento;

    /**
     * Fecha y hora en que se guardó esta respuesta.
     * Se asigna en el controller al persistir, nunca en el cliente.
     */
    private LocalDateTime fechaRespuesta;

    // ── Getters / Setters ─────────────────────────────────────────

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getTexto() { return texto; }
    public void setTexto(String texto) { this.texto = texto; }

    public Alumno getAlumno() { return alumno; }
    public void setAlumno(Alumno alumno) { this.alumno = alumno; }

    public Long getAlumnoId() { return alumnoId; }
    public void setAlumnoId(Long alumnoId) { this.alumnoId = alumnoId; }

    public Pregunta getPregunta() { return pregunta; }
    public void setPregunta(Pregunta pregunta) { this.pregunta = pregunta; }

    public Long getPreguntaId() { return preguntaId; }
    public void setPreguntaId(Long preguntaId) { this.preguntaId = preguntaId; }

    public Integer getNumeroIntento() { return numeroIntento; }
    public void setNumeroIntento(Integer numeroIntento) { this.numeroIntento = numeroIntento; }

    public LocalDateTime getFechaRespuesta() { return fechaRespuesta; }
    public void setFechaRespuesta(LocalDateTime fechaRespuesta) { this.fechaRespuesta = fechaRespuesta; }
}
