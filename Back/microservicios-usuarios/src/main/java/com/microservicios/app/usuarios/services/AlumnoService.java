package com.microservicios.app.usuarios.services;

import java.util.List;
import java.util.Optional;

import com.microservicios.commons.alumnos.models.entity.Alumno;
import com.microservicios.commons.service.CommonService;

public interface AlumnoService extends CommonService<Alumno>{
	
	/*
	//puede ser un list tambien
	public Iterable<Alumno> findAll();
	public Optional<Alumno> findById(Long id);
	public Alumno save(Alumno alumno);
	public void deleteById(Long id);
*/
	
	public List<Alumno> findByNombreOrApellido(String texto);
	
	//traer tod la info de alumnos d elisat de ids
	public Iterable<Alumno> findAllById(Iterable<Long> ids);

	public void eliminarCursoAlumnoPorId( Long id);
}
