import { Pipe, PipeTransform } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { DomSanitizer, SafeUrl } from '@angular/platform-browser';
import { Observable, of } from 'rxjs';
import { switchMap, catchError } from 'rxjs/operators';
import { BASE_ENDPOINT } from '../config/app';

/**
 * Pipe que carga la foto del alumno via HttpClient (con JWT automatico).
 * El navegador no envía headers en <img src>, por eso se carga con HttpClient.
 *
 * Uso: <img [src]="alumno.id | fotoAlumno | async">
 */
@Pipe({ name: 'fotoAlumno' })
export class FotoAlumnoPipe implements PipeTransform {

  constructor(
    private http: HttpClient,
    private sanitizer: DomSanitizer
  ) {}

  transform(alumnoId: number): Observable<SafeUrl | null> {
    if (!alumnoId) return of(null);

    return this.http
      .get(`${BASE_ENDPOINT}/alumnos/uploads/img/${alumnoId}`, { responseType: 'blob' })
      .pipe(
        switchMap(blob => {
          const url = URL.createObjectURL(blob);
          return of(this.sanitizer.bypassSecurityTrustUrl(url));
        }),
        catchError(() => of(null))
      );
  }
}
