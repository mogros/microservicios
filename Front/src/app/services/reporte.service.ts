import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { BASE_ENDPOINT } from '../config/app';

export interface ResumenGeneral {
  totalCursos: number;
  totalAlumnos: number;
  totalExamenes: number;
  promedioAlumnosPorCurso: number;
}

export interface AlumnoPorCurso {
  curso: string;
  totalAlumnos: number;
  totalExamenes: number;
  alumnosParticiparon: number;
  porcentajeParticipacion: number;
}

export interface ParticipacionExamen {
  examenId: number;
  examen: string;
  curso: string;
  totalAlumnosCurso: number;
  alumnosRespondieron: number;
  porcentajeRespuesta: number;
}

export interface ActividadAlumno {
  alumnoId: number;
  nombre: string;
  email: string;
  cursosMatriculado: number;
  examenesDisponibles: number;
  examenesRespondidos: number;
  porcentajeCompletado: number;
  estado: string;
}

export interface ResumenAsignatura {
  asignatura: string;
  totalExamenes: number;
  totalAlumnos: number;
  totalCursos: number;
  cursosInvolucrados: string[];
}

@Injectable({ providedIn: 'root' })
export class ReporteService {
  private base = `${BASE_ENDPOINT}/reportes`;
  constructor(private http: HttpClient) {}

  resumenGeneral(): Observable<ResumenGeneral> {
    return this.http.get<ResumenGeneral>(`${this.base}/resumen-general`);
  }
  alumnosPorCurso(): Observable<AlumnoPorCurso[]> {
    return this.http.get<AlumnoPorCurso[]>(`${this.base}/alumnos-por-curso`);
  }
  participacionPorExamen(): Observable<ParticipacionExamen[]> {
    return this.http.get<ParticipacionExamen[]>(`${this.base}/participacion-por-examen`);
  }
  actividadPorAlumno(): Observable<ActividadAlumno[]> {
    return this.http.get<ActividadAlumno[]>(`${this.base}/actividad-por-alumno`);
  }
  resumenPorAsignatura(): Observable<ResumenAsignatura[]> {
    return this.http.get<ResumenAsignatura[]>(`${this.base}/resumen-por-asignatura`);
  }
}
