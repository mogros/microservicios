package com.microservicios.commons.controllers;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.microservicios.commons.service.CommonService;

//@CrossOrigin({"http://localhost:4200"})
//@CrossOrigin(origins = {"http://localhost:4200"}, allowedHeaders = {"Content-Type"})

public class CommonController<E, S extends CommonService<E>> {
	
	@Autowired
	protected S service;
	
	@GetMapping			//maper una ruta url
	public ResponseEntity<?> listar(){
		return ResponseEntity.ok().body(service.findAll());
	}

	@GetMapping("/{id}")			//maper una ruta url
	public ResponseEntity<?> ver(@PathVariable long id){
		Optional<E> o= service.findById(id);
		if (o.isEmpty()) {
			return ResponseEntity.notFound().build();			//contruye la respuesta en vacio	
		}
		return ResponseEntity.ok(o.get());				//retorna el alumno
	}

	@PostMapping
	public ResponseEntity<?> crear (@Validated @RequestBody E entity, BindingResult result){
		if(result.hasErrors()) {
			return this.validar(result);
		}
		
		E entityDb=service.save(entity);
		return ResponseEntity.status(HttpStatus.CREATED).body(entityDb);
	}



	@DeleteMapping("/{id}")
	public ResponseEntity<?> eliminar(@PathVariable Long id){
		service.deleteById(id);
		return ResponseEntity.noContent().build();
	}
	
	protected ResponseEntity<?> validar(BindingResult result){
		Map<String, Object> errores=new HashMap<>();
		result.getFieldErrors().forEach(err ->{ 
			errores.put(err.getField(), 
				"El campo " + err.getField()+ " " + err.getDefaultMessage());
			});
		return ResponseEntity.badRequest().body(errores);
	}
	
	
	@GetMapping("/pagina")			//maper una ruta url
	public ResponseEntity<?> listar(Pageable pageable){
		return ResponseEntity.ok().body(service.findAllPage(pageable));
	}
	
	
	
	
}
