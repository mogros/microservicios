package com.microservicios.app.respuestas.models.entity;

import org.springframework.data.mongodb.core.mapping.Document;

import com.microservicios.commons.alumnos.models.entity.Alumno;
import com.microservicios.commons.examenesmodels.entity.Pregunta;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

import org.springframework.data.annotation.*;

//@Entity
//@Table(name="respuestas")
@Document(collection = "respuestas")
public class Respuesta {

	//@Id
	//@GeneratedValue(strategy = GenerationType.IDENTITY)
	//private Long id;
	
	@org.springframework.data.annotation.Id
	private String id;

	private String texto;
	
	
	//@ManyToOne(fetch=FetchType.LAZY)
	//ya no hay integridad referencial porque alumnos esta en postgress
	//@Transient
	//@org.springframework.data.annotation.Transient
	private Alumno alumno;
	
	//una respuetsa 1 alumno
	//@Column(name="alumno_id")
	private Long alumnoId;
	
	//@OneToOne(fetch = FetchType.LAZY)
	//@org.springframework.data.annotation.Transient
	private Pregunta pregunta;
	
	private Long preguntaId;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	
	/*
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	*/
	
	public String getTexto() {
		return texto;
	}
	public void setTexto(String texto) {
		this.texto = texto;
	}

	public Alumno getAlumno() {
		return alumno;
	}
	public void setAlumno(Alumno alumno) {
		this.alumno = alumno;
	}
	public Pregunta getPregunta() {
		return pregunta;
	}
	public void setPregunta(Pregunta pregunta) {
		this.pregunta = pregunta;
	}
	public Long getAlumnoId() {
		return alumnoId;
	}
	public void setAlumnoId(Long alumnoId) {
		this.alumnoId = alumnoId;
	}
	public Long getPreguntaId() {
		return preguntaId;
	}
	public void setPreguntaId(Long preguntaId) {
		this.preguntaId = preguntaId;
	}
	
	
	
	
}
