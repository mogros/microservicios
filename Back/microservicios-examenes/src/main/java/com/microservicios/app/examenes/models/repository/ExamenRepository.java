package com.microservicios.app.examenes.models.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.microservicios.commons.examenesmodels.entity.Examen;

public interface ExamenRepository extends CrudRepository<Examen, Long> , PagingAndSortingRepository<Examen, Long>{
	//public interface ExamenRepository extends PagingAndSortingRepository<Examen, Long>{
	
	
	@Query("Select e from Examen e where e.nombre like %?1% ")
	public List<Examen> findByNombre(String term);
	
	
	//respeusta ya no existes , es mongo
	@Query("select e.id from Pregunta p "
			+ " join p.examen  e "
			+ " where p.id in ?1 "
			+ " group by e.id ")
	public Iterable<Long> findExamenesIdsConRespuestasByPreguntaIds(Iterable<Long> prguntaIds);
}
