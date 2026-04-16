package com.microservicios.commons.service;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


public interface CommonService<E> {
	
	//Iterable puede ser un list tambien
	public Iterable<E> findAll();
	public Optional<E> findById(Long id);
	public E save(E entity);
	public void deleteById(Long id);

	//para paginar
	public Page<E> findAllPage(Pageable pageable);

}
