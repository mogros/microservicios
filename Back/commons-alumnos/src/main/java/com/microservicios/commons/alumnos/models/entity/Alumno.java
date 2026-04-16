package com.microservicios.commons.alumnos.models.entity;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;

@Entity
@Table(name ="alumnos")
public class Alumno {
	
	@Id		//indiear que es llave
	@GeneratedValue(strategy =GenerationType.IDENTITY )
	private Long id;
	
	@NotEmpty
	private String nombre;
	
	@NotEmpty
	private String apellido;
	
	@NotEmpty
	@Email
	private String email;
	
	@Column (name = "create_at" )
	@Temporal(TemporalType.TIMESTAMP)	//incluir fecha y hora
	private Date createAt;
	
	
	@PrePersist
	public void prePersist() {
		this.createAt= new Date();
	}

	@Lob				//persistira un blob
	@JsonIgnore	
	private byte[] foto;
	public Integer getFotoHashCode() {
		return (this.foto !=null) ? this.foto.hashCode(): null;	//genera un identificador unico
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

	public String getApellido() {
		return apellido;
	}

	public void setApellido(String apellido) {
		this.apellido = apellido;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Date getCreateAt() {
		return createAt;
	}

	public void setCreateAt(Date createAt) {
		this.createAt = createAt;
	}

	
	
	public byte[] getFoto() {
		return foto;
	}

	public void setFoto(byte[] foto) {
		this.foto = foto;
	}

	@Override
	public boolean equals(Object obj) {
		if(this == obj) {
			return true;
		}
		if(!(obj instanceof Alumno)) {
			return false;
		}
		
		//cast d eobject
		Alumno a=(Alumno) obj;
		
		// TODO Auto-generated method stub
		return this.id!= null && this.id.equals(a.getId());
	}
	
	
	
}
