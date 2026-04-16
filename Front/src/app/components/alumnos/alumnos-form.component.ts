import { Component, OnInit } from '@angular/core';
import { Alumno } from '../../models/alumno';
import { AlumnoService } from '../../services/alumno.service';
import { ActivatedRoute, Router } from '@angular/router';
import Swal from 'sweetalert2'
import { CommonFormComponent } from '../common-form.component';

@Component({
  selector: 'app-alumnos-form',
  templateUrl: './alumnos-form.component.html',
  styleUrl: './alumnos-form.component.css'
})
export class AlumnosFormComponent extends CommonFormComponent<Alumno, AlumnoService> implements OnInit{

    private fotoSeleccionada: File;

  constructor( service: AlumnoService, 
               router: Router,
               route: ActivatedRoute){
                    super(service,router,route);
                    this.titulo='CREAR ALUMNO';
                    this.model= new Alumno();
                    this.redirect='/alumnos';
                    this.nombreModel=Alumno.name;
               }

  
    public seleccionarFoto(event): void{
      this.fotoSeleccionada= event.target.files[0];
      console.info(this.fotoSeleccionada);

      //valdiar que sea archivo imagen
      if(this.fotoSeleccionada.type.indexOf('image')<0){
        this.fotoSeleccionada=null;
        Swal.fire('ERROR AL SELECCIONAR LA FOTO:', 'El archivo debe de ser tipo imagen','error');
      }


    }

    public override  crear(): void{
        if (!this.fotoSeleccionada){
            super.crear();
        }else{
           (this.service as AlumnoService).crearConFoto(this.model, this.fotoSeleccionada).subscribe(alumno =>{
                    console.log(alumno);
                    Swal.fire('NUEVO',`${this.nombreModel} ${alumno.nombre} creado con exito` , 'success');
                    this.router.navigate([this.redirect]);
                    }, err=>{
                      if(err.status === 400) {    //bad request
                        this.error=err.error;
                        console.log(this.error);
                      }
                    }
              );
        }
    }

    public override editar(): void{
        if (!this.fotoSeleccionada){
            super.editar();
        }else{
           (this.service as AlumnoService).editarConFoto(this.model, this.fotoSeleccionada).subscribe(alumno =>{
                    console.log(alumno);
                    Swal.fire('MODFICADO',`${this.nombreModel} ${alumno.nombre} actualizado con exito` , 'success');
                    this.router.navigate([this.redirect]);
                    }, err=>{
                      if(err.status === 400) {    //bad request
                        this.error=err.error;
                        console.log(this.error);
                      }
                    }
              );
        }
    }


}
