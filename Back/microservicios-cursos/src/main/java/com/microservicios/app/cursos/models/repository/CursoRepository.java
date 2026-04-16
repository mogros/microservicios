package com.microservicios.app.cursos.models.repository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.microservicios.app.cursos.models.entity.Curso;
import com.microservicios.commons.examenesmodels.entity.Examen;

public interface CursoRepository extends CrudRepository<Curso, Long>, PagingAndSortingRepository<Curso, Long> {
	//public interface CursoRepository extends PagingAndSortingRepository<Curso, Long> {
	
	//fetch poblar el curso con la lista d ealumnos
	//@Query("Select c from Curso c join fetch c.alumnos a where a.id=?1")
	@Query("Select c from Curso c join fetch c.cursoAlumnos a where a.alumnoId=?1")
	public Curso findCursoByAlumnoId(Long id);

	@Modifying
	@Query("delete from CursoAlumno ca where ca.alumnoId=?1")
	public void eliminarCursoAlumnoPorId(Long id);	
}
