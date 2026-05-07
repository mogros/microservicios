package com.microservicios.app.cursos.reportes;

import com.microservicios.app.cursos.clients.AlumnoFeignClient;
import com.microservicios.app.cursos.clients.RespuestaFeignClient;
import com.microservicios.app.cursos.models.entity.Curso;
import com.microservicios.app.cursos.services.CursoService;
import com.microservicios.commons.alumnos.models.entity.Alumno;
import com.microservicios.commons.examenesmodels.entity.Examen;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * Endpoints de estadísticas para el módulo de reportes.
 * Todos los datos se calculan en tiempo real combinando
 * información de ms-cursos, ms-usuarios y ms-respuestas.
 */
@RestController
@RequestMapping("/reportes")
public class ReporteController {

    @Autowired private CursoService cursoService;
    @Autowired private AlumnoFeignClient alumnoFeignClient;
    @Autowired private RespuestaFeignClient respuestaFeignClient;

    /**
     * Resumen general del sistema.
     * Total de cursos, alumnos, exámenes y tasa de participación global.
     */
    @GetMapping("/resumen-general")
    public ResponseEntity<?> resumenGeneral() {
        List<Curso> cursos = (List<Curso>) cursoService.findAll();

        long totalCursos = cursos.size();
        long totalAlumnos = cursos.stream()
                .mapToLong(c -> c.getCursoAlumnos().size())
                .sum();
        long totalExamenes = cursos.stream()
                .mapToLong(c -> c.getExamenes().size())
                .sum();
        double promedioAlumnosPorCurso = totalCursos > 0
                ? (double) totalAlumnos / totalCursos : 0;

        Map<String, Object> resumen = new LinkedHashMap<>();
        resumen.put("totalCursos", totalCursos);
        resumen.put("totalAlumnos", totalAlumnos);
        resumen.put("totalExamenes", totalExamenes);
        resumen.put("promedioAlumnosPorCurso",
                Math.round(promedioAlumnosPorCurso * 10.0) / 10.0);
        return ResponseEntity.ok(resumen);
    }

    /**
     * Alumnos por curso con porcentaje de participación en exámenes.
     * Para cada curso: cuántos alumnos han respondido al menos un examen.
     */
    @GetMapping("/alumnos-por-curso")
    public ResponseEntity<?> alumnosPorCurso() {
        List<Curso> cursos = (List<Curso>) cursoService.findAll();
        List<Map<String, Object>> resultado = new ArrayList<>();

        for (Curso curso : cursos) {
            int totalAlumnosCurso = curso.getCursoAlumnos().size();
            int totalExamenesCurso = curso.getExamenes().size();

            // Calcular cuántos alumnos respondieron al menos 1 examen
            List<Long> idsAlumnos = curso.getCursoAlumnos().stream()
                    .map(ca -> ca.getAlumnoId()).collect(Collectors.toList());

            int alumnosParticiparon = 0;
            for (Long alumnoId : idsAlumnos) {
                List<Long> examsRespondidos = StreamSupport.stream(
                        respuestaFeignClient.obtenerExamenesIdConRespuestasAlumno(alumnoId)
                                .spliterator(), false)
                        .collect(Collectors.toList());
                if (!examsRespondidos.isEmpty()) alumnosParticiparon++;
            }

            double porcentajeParticipacion = totalAlumnosCurso > 0
                    ? Math.round((double) alumnosParticiparon / totalAlumnosCurso * 1000) / 10.0
                    : 0;

            Map<String, Object> fila = new LinkedHashMap<>();
            fila.put("curso", curso.getNombre());
            fila.put("totalAlumnos", totalAlumnosCurso);
            fila.put("totalExamenes", totalExamenesCurso);
            fila.put("alumnosParticiparon", alumnosParticiparon);
            fila.put("porcentajeParticipacion", porcentajeParticipacion);
            resultado.add(fila);
        }

        return ResponseEntity.ok(resultado);
    }

    /**
     * Participación por examen: cuántos alumnos respondieron cada examen
     * y cuántos intentos promedio realizaron.
     */
    @GetMapping("/participacion-por-examen")
    public ResponseEntity<?> participacionPorExamen() {
        List<Curso> cursos = (List<Curso>) cursoService.findAll();
        Map<Long, Map<String, Object>> examenesMap = new LinkedHashMap<>();

        for (Curso curso : cursos) {
            List<Long> idsAlumnos = curso.getCursoAlumnos().stream()
                    .map(ca -> ca.getAlumnoId()).collect(Collectors.toList());

            for (Examen examen : curso.getExamenes()) {
                examenesMap.computeIfAbsent(examen.getId(), k -> {
                    Map<String, Object> m = new LinkedHashMap<>();
                    m.put("examenId", examen.getId());
                    m.put("examen", examen.getNombre());
                    m.put("curso", curso.getNombre());
                    m.put("totalAlumnosCurso", idsAlumnos.size());
                    m.put("alumnosRespondieron", 0);
                    m.put("porcentajeRespuesta", 0.0);
                    return m;
                });

                int respondieron = 0;
                for (Long alumnoId : idsAlumnos) {
                    List<Long> examsRespondidos = StreamSupport.stream(
                            respuestaFeignClient.obtenerExamenesIdConRespuestasAlumno(alumnoId)
                                    .spliterator(), false)
                            .collect(Collectors.toList());
                    if (examsRespondidos.contains(examen.getId())) respondieron++;
                }

                int total = idsAlumnos.size();
                double pct = total > 0
                        ? Math.round((double) respondieron / total * 1000) / 10.0 : 0;

                examenesMap.get(examen.getId()).put("alumnosRespondieron", respondieron);
                examenesMap.get(examen.getId()).put("porcentajeRespuesta", pct);
            }
        }

        return ResponseEntity.ok(new ArrayList<>(examenesMap.values()));
    }

    /**
     * Actividad por alumno: cuántos exámenes respondió cada alumno
     * y en cuántos cursos está matriculado.
     */
    @GetMapping("/actividad-por-alumno")
    public ResponseEntity<?> actividadPorAlumno() {
        List<Curso> cursos = (List<Curso>) cursoService.findAll();

        // Recopilar todos los alumnoIds únicos
        Set<Long> todosIds = cursos.stream()
                .flatMap(c -> c.getCursoAlumnos().stream().map(ca -> ca.getAlumnoId()))
                .collect(Collectors.toSet());

        if (todosIds.isEmpty()) return ResponseEntity.ok(List.of());

        // Obtener datos de alumnos desde ms-usuarios
        List<Alumno> alumnos = StreamSupport.stream(
                alumnoFeignClient.obtenerAlumnosPorCurso(todosIds).spliterator(), false)
                .collect(Collectors.toList());

        List<Map<String, Object>> resultado = new ArrayList<>();

        for (Alumno alumno : alumnos) {
            // Contar en cuántos cursos está
            long cursosMatriculado = cursos.stream()
                    .filter(c -> c.getCursoAlumnos().stream()
                            .anyMatch(ca -> ca.getAlumnoId().equals(alumno.getId())))
                    .count();

            // Contar exámenes respondidos
            List<Long> examsRespondidos = StreamSupport.stream(
                    respuestaFeignClient.obtenerExamenesIdConRespuestasAlumno(alumno.getId())
                            .spliterator(), false)
                    .collect(Collectors.toList());

            // Total de exámenes disponibles para este alumno
            long examenesDisponibles = cursos.stream()
                    .filter(c -> c.getCursoAlumnos().stream()
                            .anyMatch(ca -> ca.getAlumnoId().equals(alumno.getId())))
                    .mapToLong(c -> c.getExamenes().size())
                    .sum();

            double porcentajeCompletado = examenesDisponibles > 0
                    ? Math.round((double) examsRespondidos.size() / examenesDisponibles * 1000) / 10.0
                    : 0;

            Map<String, Object> fila = new LinkedHashMap<>();
            fila.put("alumnoId", alumno.getId());
            fila.put("nombre", alumno.getNombre() + " " + alumno.getApellido());
            fila.put("email", alumno.getEmail());
            fila.put("cursosMatriculado", cursosMatriculado);
            fila.put("examenesDisponibles", examenesDisponibles);
            fila.put("examenesRespondidos", examsRespondidos.size());
            fila.put("porcentajeCompletado", porcentajeCompletado);
            // Estado: aprobado si completó >= 60%
            fila.put("estado", porcentajeCompletado >= 60 ? "Aprobado" : "En progreso");
            resultado.add(fila);
        }

        // Ordenar por porcentaje completado descendente
        resultado.sort((a, b) ->
                Double.compare((double) b.get("porcentajeCompletado"),
                               (double) a.get("porcentajeCompletado")));

        return ResponseEntity.ok(resultado);
    }

    /**
     * Tendencia mensual: número de exámenes respondidos por mes.
     * Útil para ver actividad a lo largo del tiempo.
     * (Datos simulados por mes basados en alumnos y cursos activos,
     *  ya que ms-respuestas es MongoDB y no tenemos endpoint de agrupación por fecha aún)
     */
    @GetMapping("/resumen-por-asignatura")
    public ResponseEntity<?> resumenPorAsignatura() {
        List<Curso> cursos = (List<Curso>) cursoService.findAll();
        Map<String, Map<String, Object>> porAsignatura = new LinkedHashMap<>();

        for (Curso curso : cursos) {
            for (Examen examen : curso.getExamenes()) {
                String asignatura = examen.getAsignaturaPadre() != null
                        ? examen.getAsignaturaPadre().getNombre() : "Sin asignatura";

                porAsignatura.computeIfAbsent(asignatura, k -> {
                    Map<String, Object> m = new LinkedHashMap<>();
                    m.put("asignatura", k);
                    m.put("totalExamenes", 0);
                    m.put("totalAlumnos", 0);
                    m.put("cursosInvolucrados", new HashSet<String>());
                    return m;
                });

                Map<String, Object> entry = porAsignatura.get(asignatura);
                entry.put("totalExamenes", (int) entry.get("totalExamenes") + 1);
                entry.put("totalAlumnos",
                        (int) entry.get("totalAlumnos") + curso.getCursoAlumnos().size());
                ((Set<String>) entry.get("cursosInvolucrados")).add(curso.getNombre());
            }
        }

        // Convertir Set a List para serialización JSON
        List<Map<String, Object>> resultado = porAsignatura.values().stream()
                .map(entry -> {
                    Set<String> cursosSet = (Set<String>) entry.get("cursosInvolucrados");
                    entry.put("cursosInvolucrados", new ArrayList<>(cursosSet));
                    entry.put("totalCursos", cursosSet.size());
                    return entry;
                })
                .collect(Collectors.toList());

        return ResponseEntity.ok(resultado);
    }
}
