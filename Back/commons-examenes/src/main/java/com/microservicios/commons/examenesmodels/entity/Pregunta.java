package com.microservicios.commons.examenesmodels.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;


import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name="preguntas")
public class Pregunta {


	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private  Long id;
	
	private String texto;

	@JsonIgnoreProperties({"preguntas"})  //para eviatr loop infinito
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name ="examen_id")
	private Examen examen;
	
	
	public Examen getExamen() {
		return examen;
	}

	public void setExamen(Examen examen) {
		this.examen = examen;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getTexto() {
		return texto;
	}

	public void setTexto(String texto) {
		this.texto = texto;
	}

	@Override
	public boolean equals(Object obj) {
		if(this == obj) {
			return true;
		}
		if(!(obj instanceof Pregunta)) {
			return false;
		}
		
		//cast d eobject
		Pregunta a=(Pregunta) obj;
		
		// TODO Auto-generated method stub
		return this.id!= null && this.id.equals(a.getId());
	}
	
	
	
	
	
}
