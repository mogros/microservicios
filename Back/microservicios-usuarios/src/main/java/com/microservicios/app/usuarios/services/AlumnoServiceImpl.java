package com.microservicios.app.usuarios.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.microservicios.app.usuarios.client.CursoFeignClient;
import com.microservicios.app.usuarios.models.repository.AlumnoRepository;
import com.microservicios.commons.alumnos.models.entity.Alumno;
import com.microservicios.commons.service.CommonServiceImpl;

@Service
public class AlumnoServiceImpl extends CommonServiceImpl<Alumno, AlumnoRepository> implements AlumnoService {

	@Autowired
	private CursoFeignClient clientCurso;
	
	@Override
	@Transactional(readOnly = true)		//solo consulta
	public List<Alumno> findByNombreOrApellido(String texto) {
		return repository.findByNombreOrApellido(texto);
	}

	@Override
	@Transactional(readOnly = true)		//solo consulta
	public Iterable<Alumno> findAllById(Iterable<Long> ids) {
		return repository.findAllById(ids);
	}

	
	@Override
	public void eliminarCursoAlumnoPorId(Long id) {
		clientCurso.eliminarCursoAlumnoPorId(id);
	}

	//sobrescribir el metodod
	@Override
	@Transactional
	public void deleteById(Long id) {
		// TODO Auto-generated method stub
		super.deleteById(id);
		this.eliminarCursoAlumnoPorId(id);
	}

	@Override
	@Transactional(readOnly = true)		//solo consulta
	public Iterable<Alumno> findAll() {
		return  repository.findAllByOrderByIdAsc();
	}

	@Override
	@Transactional(readOnly = true)		//solo consulta
	public Page<Alumno> findAllPage(Pageable pageable) {
		return repository.findAllByOrderByIdAsc(pageable);
	}

	
	
	//@Autowired
	//private AlumnoRepository repository;
	
	/*
	@Override
	@Transactional(readOnly = true)				//importar de spring framework
	public Iterable<Alumno> findAll() {
		// TODO Auto-generated method stub
		return repository.findAll();
	}

	@Override
	@Transactional(readOnly = true)	
	public Optional<Alumno> findById(Long id) {
		// TODO Auto-generated method stub
		return repository.findById(id);
	}

	@Override
	@Transactional
	public Alumno save(Alumno alumno) {
		// TODO Auto-generated method stub
		return repository.save(alumno);
	}

	@Override
	@Transactional
	public void deleteById(Long id) {
		// TODO Auto-generated method stub
		repository.deleteById(id);
	}
	*/
}
