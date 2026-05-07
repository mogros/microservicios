import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { BASE_ENDPOINT } from '../config/app';
import { Alumno } from '../models/alumno';
import { Examen } from '../models/examen';
import { Respuesta, ResumenIntentos } from '../models/respuesta';

@Injectable({ providedIn: 'root' })
export class RespuestaService {

  private cabeceras = new HttpHeaders({ 'Content-Type': 'application/json' });
  private baseEndPoint = `${BASE_ENDPOINT}/respuestas`;

  constructor(private http: HttpClient) {}

  /** Envía un intento completo de examen */
  crear(respuestas: Respuesta[]): Observable<Respuesta[]> {
    return this.http.post<Respuesta[]>(this.baseEndPoint, respuestas, { headers: this.cabeceras });
  }

  /** Todas las respuestas de un alumno en un examen (todos los intentos) */
  obtenerRespuestasPorAlumnoPorExamen(alumno: Alumno, examen: Examen): Observable<Respuesta[]> {
    return this.http.get<Respuesta[]>(
      `${this.baseEndPoint}/alumno/${alumno.id}/examen/${examen.id}`
    );
  }

  /** Respuestas de un intento concreto */
  obtenerRespuestasPorIntento(alumno: Alumno, examen: Examen, intento: number): Observable<Respuesta[]> {
    return this.http.get<Respuesta[]>(
      `${this.baseEndPoint}/alumno/${alumno.id}/examen/${examen.id}/intento/${intento}`
    );
  }

  /** Resumen de intentos: total, siguiente número */
  obtenerResumenIntentos(alumno: Alumno, examen: Examen): Observable<ResumenIntentos> {
    return this.http.get<ResumenIntentos>(
      `${this.baseEndPoint}/alumno/${alumno.id}/examen/${examen.id}/intentos`
    );
  }
}
