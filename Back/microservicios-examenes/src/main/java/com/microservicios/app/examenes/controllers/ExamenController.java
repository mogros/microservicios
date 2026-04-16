package com.microservicios.app.examenes.controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.microservicios.app.examenes.services.ExamenService;
import com.microservicios.commons.controllers.CommonController;
import com.microservicios.commons.examenesmodels.entity.Examen;
import com.microservicios.commons.examenesmodels.entity.Pregunta;

import jakarta.validation.Valid;

@RestController
public class ExamenController extends CommonController<Examen, ExamenService>{

	
	@GetMapping("/respondidos-por-preguntas")
	public ResponseEntity<?> obtenerExamenesIdsPorPreguntasIdRespondidas(
			@RequestParam List<Long> prguntaIds){
		return ResponseEntity.ok().body(service.findExamenesIdsConRespuestasByPreguntaIds(prguntaIds));
	}
	
	@PutMapping("/{id}")
	public ResponseEntity<?> editar(@Valid @RequestBody Examen examen, BindingResult result 
			, @PathVariable Long id){
		
		/*if(result.hasErrors()) {
			return this.validar(result);
		}
		
		Optional<Examen> o =service.findById(id);
		if (! o.isPresent()) {
			return ResponseEntity.notFound().build();	//respuesta vacia
		}
		Examen examenDb=o.get();
		examenDb.setNombre(examen.getNombre());
		examenDb.setAsignaturaHija(examen.getAsignaturaHija());
		examenDb.setAsignaturaPadre(examen.getAsignaturaPadre());
		
		// Usar removeIf que está diseñado para este propósito
	    examenDb.getPreguntas().removeIf(pdb -> !examen.getPreguntas().contains(pdb));
	    // Agregar las nuevas preguntas que no existen
	    for (Pregunta pregunta : examen.getPreguntas()) {
	        if (!examenDb.getPreguntas().contains(pregunta)) {
	            examenDb.addPregunta(pregunta);
	        }
	    }
	    
		return ResponseEntity.status(HttpStatus.CREATED).body(service.save(examenDb));
	    
	    */
		
		if(result.hasErrors()) {
			return this.validar(result);
		}
		
		Optional<Examen> o = service.findById(id);
		
		if(!o.isPresent()) {
			return ResponseEntity.notFound().build();
		}
		Examen examenDb = o.get();
		examenDb.setNombre(examen.getNombre());
		
		List<Pregunta> eliminadas = examenDb.getPreguntas()
		.stream()
		.filter(pdb -> !examen.getPreguntas().contains(pdb))
		.collect(Collectors.toList());
		
		
		eliminadas.forEach(examenDb::removePregunta);
		
		examenDb.setPreguntas(examen.getPreguntas());
		examenDb.setAsignaturaHija(examen.getAsignaturaHija());
		examenDb.setAsignaturaPadre(examen.getAsignaturaPadre());
		
		return ResponseEntity.status(HttpStatus.CREATED).body(service.save(examenDb));

					/*  priemra forma
					List<Pregunta> eliminadas= new ArrayList<>();
					examenDb.getPreguntas().forEach(pdb ->{
						if(!examen.getPreguntas().contains(pdb)) {	//si no existe la eliminamos<
							eliminadas.add(pdb);
						}
					});
					
					*/
		
					////segudna forma
					/*
					examenDb.getPreguntas()
						.stream()
						.filter(pdb -> !examen.getPreguntas().contains(pdb))
						.forEach(examenDb::removePregunta)
						;
					*/
					
					/* priemra forma
					eliminadas.forEach(p -> {
						examenDb.removePregunta(p);
					});
					*/
					
					//segunda forma
					//eliminadas.forEach(examenDb::removePregunta);
					
					//examenDb.setPreguntas(examen.getPreguntas());
					
		
		
		
	
	    
	}
	
	
	@GetMapping("/filtrar/{term}")
	public ResponseEntity<?> filtrar(@PathVariable String term){
		return ResponseEntity.ok(service.findByNombre(term));
	}
	
	
	@GetMapping("/asignaturas")
	public ResponseEntity<?> listarAsignaturas(){
		return ResponseEntity.ok(service.findAllAsignaturas());
	}
	
}
