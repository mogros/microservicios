package com.microservicios.app.cursos.services;



import com.microservicios.app.cursos.models.entity.Curso;
import com.microservicios.commons.alumnos.models.entity.Alumno;
import com.microservicios.commons.service.CommonService;

public interface CursoService extends CommonService<Curso> {

	public Iterable<Long> obtenerExamenesIdConRespuestasAlumno( Long alumnoId);
	public Iterable<Alumno> obtenerAlumnosPorCurso( Iterable<Long> ids);
	
	public Curso findCursoByAlumnoId(Long id);
	public void eliminarCursoAlumnoPorId(Long id);
}
