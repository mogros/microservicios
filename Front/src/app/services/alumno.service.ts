import { BASE_ENDPOINT } from './../config/app';
import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { Alumno } from '../models/alumno';
import { map} from 'rxjs/operators';
import { CommonService } from './common.service';

@Injectable({
  providedIn: 'root'
})
export class AlumnoService extends CommonService<Alumno>{

  protected override baseEndPoint= BASE_ENDPOINT+ '/alumnos';

  constructor( http: HttpClient) {
      super(http);
   }


  //private cabeceras: HttpHeaders= new HttpHeaders({'Content-Type':'application/json'});

  //constructor(private http: HttpClient) { }

  ////obtener observable
  //public listar(): Observable<Alumno[]>{
  //  return this.http.get<Alumno[]>(this.baseEndPoint);
    
    /*
    return this.http.get(this.baseEndPoint).pipe(
      map(alumnos=> {
        return alumnos as Alumno[];
      })
    );
    */
  //}

  //************************************************ */
/*
  public listarPaginas(page: string, size: string): Observable<any>{
    const params = new HttpParams()
    .set('page', page)
    .set('size', size);
    return this.http.get<any>(`${this.baseEndPoint}/pagina`, {params: params});
  } 

  public ver(id: number): Observable<Alumno> {
    //una forma
    //return this.http.get<Alumno>(this.baseEndPoint+'/'+id);

    return this.http.get<Alumno>( `${this.baseEndPoint}/${id} `);
  }

  public crear (alumno: Alumno): Observable<Alumno>{
    return this.http.post<Alumno>(this.baseEndPoint, alumno, {headers: this.cabeceras});
  }
  public editar (alumno: Alumno): Observable<Alumno>{
    return this.http.put<Alumno>(`${this.baseEndPoint}/${alumno.id} `, alumno, {headers: this.cabeceras});    
  }

  public eliminar(id: number): Observable<void>{
    return this.http.delete<void>(`${this.baseEndPoint}/${id} `);
  }
    */

   public crearConFoto(alumno: Alumno, archivo: File): Observable<Alumno>{
      const formData = new FormData;
      formData.append('archivo', archivo);
      formData.append('nombre', alumno.nombre);
      formData.append('apellido', alumno.apellido);
      formData.append('email', alumno.email);
      return this.http.post<Alumno>(this.baseEndPoint + '/crear-con-foto', formData);
   }
   
   public editarConFoto(alumno: Alumno, archivo: File): Observable<Alumno>{
      const formData = new FormData;
      formData.append('archivo', archivo);
      formData.append('nombre', alumno.nombre);
      formData.append('apellido', alumno.apellido);
      formData.append('email', alumno.email);
      return this.http.put<Alumno>(`${this.baseEndPoint}/editar-con-foto/${alumno.id}`, formData);
   }

   public filtrarPorNombre(nombre: string): Observable<Alumno[]>{
      return this.http.get<Alumno[]>(`${this.baseEndPoint}/filtrar/${nombre}`);
   }

}
