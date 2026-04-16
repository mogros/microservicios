package com.microservicios.commons.examenesmodels.entity;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name ="asignaturas")
public class Asignatura {

	@Id
	@GeneratedValue(strategy =GenerationType.IDENTITY )
	private Long id;
	private String nombre;
	
	//muchos elementos hijos a un padre
	@JsonIgnoreProperties(value = {"hijos","handler" ,"hibernateLazyInitializer"})
	@ManyToOne(fetch =FetchType.LAZY)
	private Asignatura padre;
	
	//si borras a un padre borras a sus hijos
	@JsonIgnoreProperties(value = {"padre" ,"handler","hibernateLazyInitializer"} , allowSetters = true)
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "padre", cascade = CascadeType.ALL)
	private List<Asignatura> hijos;
	
	
	
	public Asignatura() {
		this.hijos=new ArrayList<>();
	}
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getNombre() {
		return nombre;
	}
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	
	public Asignatura getPadre() {
		return padre;
	}
	public void setPadre(Asignatura padre) {
		this.padre = padre;
	}
	public List<Asignatura> getHijos() {
		return hijos;
	}
	
	public void setHijos(List<Asignatura> hijos) {
		this.hijos = hijos;
	}
	
	
	
	
}
