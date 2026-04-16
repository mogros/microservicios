package com.microservicios.app.cursos.controllers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.microservicios.app.cursos.models.entity.Curso;
import com.microservicios.app.cursos.models.entity.CursoAlumno;
import com.microservicios.app.cursos.services.CursoService;
import com.microservicios.commons.alumnos.models.entity.Alumno;
import com.microservicios.commons.controllers.CommonController;
import com.microservicios.commons.examenesmodels.entity.Examen;

import jakarta.validation.Valid;

@RestController
public class CursoController extends CommonController<Curso, CursoService > {

	
	//del properties
	@Value("${config.balanceador.test}")
	private String balanceadorTest;

	@GetMapping			//maper una ruta url
	@Override
	public ResponseEntity<?> listar(){
		List<Curso> cursos = ((List<Curso>) service.findAll()).stream().map(c ->{
						c.getCursoAlumnos().forEach(ca->{
						Alumno alumno= new Alumno();
						alumno.setId(ca.getAlumnoId());
						c.addAlumno(alumno);
					});
					return c;
				}).collect(Collectors.toList()) ;
		
		//return ResponseEntity.ok().body(service.findAll());
		return ResponseEntity.ok().body(cursos);
	}
	
	
	@GetMapping("/pagina")			//maper una ruta url
	@Override
	public ResponseEntity<?> listar(Pageable pageable){
		//Page de data.domain
		Page<Curso> cursos=service.findAllPage(pageable).map(curso->{
						curso.getCursoAlumnos().forEach(ca->{
						Alumno alumno= new Alumno();
						alumno.setId(ca.getAlumnoId());
						curso.addAlumno(alumno);
					});
			return curso;
		});
		return ResponseEntity.ok().body(cursos);
	}
	
	
	
	
	@GetMapping("/{id}")			//maper una ruta url
	@Override
	public ResponseEntity<?> ver(@PathVariable long id){
		Optional<Curso> o= service.findById(id);
		if (o.isEmpty()) {
			return ResponseEntity.notFound().build();			//contruye la respuesta en vacio	
		}
		Curso curso = o.get();
		//solo se manejan los ids de los alumnos en US usuarios
		if(curso.getCursoAlumnos().isEmpty()==false) {
			//convertir ids a long
			List<Long> ids=curso.getCursoAlumnos().stream().map(ca ->
				 	ca.getAlumnoId()).collect(Collectors.toList());
			//obteern alumnos
			List<Alumno> alumnos= (List<Alumno>) service.obtenerAlumnosPorCurso(ids);
			curso.setAlumnos(alumnos);
		}
		
		return ResponseEntity.ok().body(curso);				//retorna 
	}
	
	@GetMapping("balanceador-test")
	public ResponseEntity<?> balamnceadorTest() {
		Map<String, Object> response= new HashMap<String, Object>();
		response.put("balanceador", balanceadorTest);
		response.put("cursos", service.findAll());
		
		return ResponseEntity.ok(response);
	}

	@PutMapping("/{id}")
	public ResponseEntity<?> editar(@Valid @RequestBody Curso curso, BindingResult result
			, @PathVariable Long id){
		
		if(result.hasErrors()) {
			return this.validar(result);
		}
		
		Optional<Curso> o= this.service.findById(id);
		if(! o.isPresent()) {
			return ResponseEntity.notFound().build();		//se manda un 404
		}
		
		Curso dbCurso=o.get();
		dbCurso.setNombre(curso.getNombre());
		
		return ResponseEntity.status(HttpStatus.CREATED).body(this.service.save(dbCurso));
	}
	
	@PutMapping("/{id}/asignar-alumnos")
	public ResponseEntity<?> asignarAlumnos(@RequestBody List<Alumno> alumnos,
			@PathVariable Long id ){
		
		Optional<Curso> o= this.service.findById(id);
		if(! o.isPresent()) {
			return ResponseEntity.notFound().build();		//se manda un 404
		}
		Curso dbCurso=o.get();
		alumnos.forEach(a->{
			
	//modifcar porque en vez de agregar unañumno se debe agregar alumno curso
			CursoAlumno cursoAlumno= new CursoAlumno();
			cursoAlumno.setAlumnoId(a.getId());
			cursoAlumno.setCurso(dbCurso);
			
			//dbCurso.addAlumno(a);
			dbCurso.addCursoAlumno(cursoAlumno);
			});
		return ResponseEntity.status(HttpStatus.CREATED).body(this.service.save(dbCurso));
	}
	

	@PutMapping("/{id}/eliminar-alumno")
	public ResponseEntity<?> eliminarAlumno(@RequestBody Alumno alumno, @PathVariable Long id ){
		Optional<Curso> o= this.service.findById(id);
		if(! o.isPresent()) {
			return ResponseEntity.notFound().build();		//se manda un 404
		}
		Curso dbCurso=o.get();

		//modifcar porque en vez de agregar unañumno se debe agregar alumno curso
		CursoAlumno cursoAlumno= new CursoAlumno();
		cursoAlumno.setAlumnoId(alumno.getId());
		
		//dbCurso.removeAlumno(alumno);
		dbCurso.removeCursoAlumno(cursoAlumno) ;
		
		return ResponseEntity.status(HttpStatus.CREATED).body(this.service.save(dbCurso));
	}

	@GetMapping("/alumno/{id}")
	public ResponseEntity<?> buscarAlumnoPorId(@PathVariable Long id){
		Curso curso=service.findCursoByAlumnoId(id);
		if (curso != null) {
			List<Long> examenesIds= (List<Long>) service.obtenerExamenesIdConRespuestasAlumno(id);
			if(examenesIds!=null && examenesIds.size()>0) {
				//iterar entre sus examens	, usar Stream 
				List<Examen> examenes = curso.getExamenes().stream().map(examen -> {
					if(examenesIds.contains(examen.getId())) {
						examen.setRespondido(true);
					}
					return examen;
				}).collect(Collectors.toList());
				
				curso.setExamenes(examenes);
			}
		}
		
		return ResponseEntity.ok(curso);
	}
	
	
	
	@PutMapping("/{id}/asignar-examenes")
	public ResponseEntity<?> asignarExamenes(@RequestBody List<Examen> examenes,
			@PathVariable Long id ){
		
		Optional<Curso> o= this.service.findById(id);
		if(! o.isPresent()) {
			return ResponseEntity.notFound().build();		//se manda un 404
		}
		Curso dbCurso=o.get();
		examenes.forEach(e->{
			dbCurso.addExamen(e);
			});
		return ResponseEntity.status(HttpStatus.CREATED).body(this.service.save(dbCurso));
	}
	

	@PutMapping("/{id}/eliminar-examen")
	public ResponseEntity<?> eliminarExamen(@RequestBody Examen examen, @PathVariable Long id ){
		Optional<Curso> o= this.service.findById(id);
		if(! o.isPresent()) {
			return ResponseEntity.notFound().build();		//se manda un 404
		}
		Curso dbCurso=o.get();
		dbCurso.removeExamen(examen);
		return ResponseEntity.status(HttpStatus.CREATED).body(this.service.save(dbCurso));
	}
	
	@DeleteMapping("/eliminar-alumno/{id}")
	public ResponseEntity<?> eliminarCursoPorId(@PathVariable Long id){
		service.eliminarCursoAlumnoPorId(id);
		return ResponseEntity.noContent().build();
	}
	
}
