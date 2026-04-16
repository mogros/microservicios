import { Examen } from './../../models/examen';
import { Component, OnInit } from '@angular/core';
import { CommonFormComponent } from '../common-form.component';
import { ExamenService } from '../../services/examen.service';
import { ActivatedRoute, Router } from '@angular/router';
import { Asignatura } from '../../models/asignatura';
import { Pregunta } from '../../models/pregunta';
import Swal from 'sweetalert2';

@Component({
  selector: 'app-examen-form',
  templateUrl: './examen-form.component.html',
  styleUrl: './examen-form.component.css'
})
export class ExamenFormComponent extends CommonFormComponent<Examen, ExamenService> implements OnInit {

    asignaturasPadre: Asignatura[]=[];
    asignaturasHija: Asignatura[]=[];
    errorPreguntas: string;

      constructor( service: ExamenService, 
                     router: Router,
                     route: ActivatedRoute){
                          super(service,router,route);
                          this.titulo='CREAR EXAMEN';
                          this.model= new Examen();
                          this.nombreModel=Examen.name;
                          this.redirect='/examenes';
                     }

    override ngOnInit(){
          //obtener parametros d ela ruta
          this.route.paramMap.subscribe(params=>{
              const id: number = +params.get('id');     //el + convierte a numero
              if(id){
                this.service.ver(id).subscribe(m => {
                  this.model=m;
                  this.titulo='Editar ' + this.nombreModel;

                  /*
                  (this.service as ExamenService).findAllAsignaturas().subscribe(asignaturas =>
                                                                      this.asignaturasHija= asignaturas.filter(
                                                                                a => a.padre && a.padre.id=== this.model.asignaturaPadre.id)
                  );
                  */
                 //se optimiza con esto
                 this.cargarHijos();

                });
              }
            });

            
            (this.service as ExamenService).findAllAsignaturas().subscribe(asignaturas => 
                          this.asignaturasPadre = asignaturas.filter(a => !a.padre) ); //asigantura no tenga padre

    }

    public override crear(): void{
        if(this.model.preguntas.length===0){
          //si no hya texto en preguntas sales
          this.errorPreguntas= 'Examen debe tener preguntas';
          //Swal.fire('ERROR PREGUNTAS', 'Examen debe tener preguntas','error');
          return;
        }
        this.errorPreguntas=undefined;
        this.eliminarPreguntasVacias();
        super.crear();
    }

    public override editar(): void{
        if(this.model.preguntas.length===0){
          //si no hya texto en preguntas sales
          this.errorPreguntas= 'Examen debe tener preguntas';
          //Swal.fire('ERROR PREGUNTAS', 'Examen debe tener preguntas','error');
          return;
        }
        this.errorPreguntas=undefined;
        this.eliminarPreguntasVacias();
        super.editar();
    }

    public cargarHijos(): void{
      //si no hay hijos damosareglo vacio ara q no d eerror
      this.asignaturasHija = this.asignaturasPadre? this.model.asignaturaPadre.hijos : [];
    }

    compararAsignatura(a1: Asignatura,a2: Asignatura,): boolean{
        if(a1===undefined && a2 === undefined){
          return true;
        }

        //como angualr es asincrono hay q validar la carga 
        /*
            if(a1===undefined || a1===null || a2===undefined || a2===null ){
              return false;
            }
            if(a1.id===a2.id){
              return true;
            }
        */

        return (a1===undefined || a1===null || a2===undefined || a2===null )? false : (a1.id===a2.id);
    }

    agregarPregunta(): void{
      this.model.preguntas.push(new Pregunta);
    }

    asignarTexto(pregunta: Pregunta,event: any): void{
        pregunta.texto= event.target.value as string;
        console.log(this.model);
    }

    eliminarPregunta(pregunta): void{
        this.model.preguntas= this.model.preguntas.filter(p => pregunta.texto !== p.texto);
    }

    eliminarPreguntasVacias(): void{
        this.model.preguntas = this.model.preguntas.filter(p => p.texto!= null && p.texto.length>0);
    }
}
