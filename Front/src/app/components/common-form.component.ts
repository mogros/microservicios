import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import Swal from 'sweetalert2'
import { CommonService } from '../services/common.service';
import { Generic } from '../models/generic';

@Component({
  template: '' // Template vacío para clases abstractas
})

export abstract class CommonFormComponent<E extends Generic, S extends CommonService<E>> implements OnInit{

  titulo: string;
  model: E ;
  error: any;
  protected redirect: string; 
  protected nombreModel: string; 

  constructor(protected service: CommonService<E>, 
              protected router: Router,
              protected route: ActivatedRoute){}

  ngOnInit(){
    //obtener parametros d ela ruta
    this.route.paramMap.subscribe(params=>{
        const id: number = +params.get('id');     //el + convierte a numero
        if(id){
          this.service.ver(id).subscribe(m => {
            this.model=m;
            this.titulo='Editar ' + this.nombreModel;
          });
        }
      });
  }

  public crear(): void{
      this.service.crear(this.model).subscribe(m =>{
          console.log(m);
          Swal.fire('NUEVO',`${this.nombreModel} ${m.nombre} creado con exito` , 'success');
          this.router.navigate([this.redirect]);
          }, err=>{
            if(err.status === 400) {    //bad request
              this.error=err.error;
              console.log(this.error);
            }
          }
    );
  }

    public editar(): void{
      this.service.editar(this.model).subscribe(m =>{
          console.log(m);
          //alert(`Alumno ${alumno.nombre} actualizado con exito`);
          Swal.fire('EDITAR: ',`${this.nombreModel} ${m.nombre} actualizado con exito` , 'success');
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
