package com.microservicios.app.respuestas.models.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.microservicios.app.respuestas.models.entity.Respuesta;

//public interface RespuestaRepository extends CrudRepository<Respuesta, Long> {
public interface RespuestaRepository extends MongoRepository<Respuesta, String> {

	//consulata a ivel de objeto no de tabla
	//fecth trae todos los obejstos relacionados

	/* el alumno ya no es parte de la clase

	@Query(" select r from Respuesta r "
			+ " join fetch r.alumno a "
			+ " join fetch r.pregunta p "
			+ " join fetch p.examen e "
			+ " where a.id=?1 and e.id=?2 ")

	 * */
	
	
	/* jpa  ahora usamos mongo
	@Query(" select r from Respuesta r "
			+ "  "
			+ " join fetch r.pregunta p "
			+ " join fetch p.examen e "
			+ " where r.alumnoId=?1 and e.id=?2 ")
	public Iterable<Respuesta> findRespuestaByAlumnoByExamen(Long alumnoId, Long examenId);

	*/
	
	
	//los id de los examens respodndidos
	/* alumno ya nos es árte d ela clase
	 
	 	@Query("select e.id from Respuesta r "
			+ " join r.alumno a "
			+ " join r.pregunta p "
			+ " join p.examen  e "
			+ " where a.id=?1 "
			+ " group by e.id ")

	 * */
	
	
	/* jpa  ahora usamos mongo
	
	@Query("select e.id from Respuesta r "
			+ " "
			+ " join r.pregunta p "
			+ " join p.examen  e "
			+ " where r.alumnoId=?1 "
			+ " group by e.id ")
	public Iterable<Long> findExamenesIdsConRespuestasByAlumno(Long alumnoId);
	 */	
	
	
	//tenemos la pregunta pero no el examen
	//query de mongo, in busca en un conjunto de preguntas
	@Query("{'alumnoId':?0, 'preguntaId': {$in: ?1} }")
	public Iterable<Respuesta> findRespuestaByAlumnoByPreguntaIds(Long alumnoId,Iterable<Long> preguntaIds);

	
	//buscar respeusta de alumno
	@Query("{'alumnoId':?0}")
	public Iterable<Respuesta> findByAlumnoId(Long alumnoId);

	//buscar la respusat por alumno id
	@Query("{'alumnoId':?0,'pregunta.examen.id': ?1 }")
	public Iterable<Respuesta> findRespuestaByAlumnoByExamen(Long alumnoId, Long examenId);

	//fields que campos queiro q me retrne el json
	@Query(value="{'alumnoId':?0}", fields="{'pregunta.examen.id': 1}")
	public Iterable<Respuesta> findExamenesIdsConRespuestasByAlumno(Long alumnoId);
	
}
