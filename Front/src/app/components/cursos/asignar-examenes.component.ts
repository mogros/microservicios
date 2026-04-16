import { Component, OnInit, ViewChild } from '@angular/core';
import { Curso } from '../../models/curso';
import { ActivatedRoute, Router } from '@angular/router';
import { CursoService } from '../../services/curso.service';
import { ExamenService } from '../../services/examen.service';
import { MatFormField } from "@angular/material/form-field";
import { A11yModule } from "@angular/cdk/a11y";
import { MatInput } from "@angular/material/input";
import { FormControl, ReactiveFormsModule } from '@angular/forms';
import { Examen } from '../../models/examen';
import { map, flatMap } from 'rxjs/operators';
import { MatAutocomplete, MatAutocompleteSelectedEvent } from '@angular/material/autocomplete';
import Swal from 'sweetalert2';
import { MatTableDataSource } from '@angular/material/table';
import { MatPaginator } from '@angular/material/paginator';

@Component({
  selector: 'app-asignar-examenes',
  templateUrl: './asignar-examenes.component.html',
  styleUrl: './asignar-examenes.component.css'
})
export class AsignarExamenesComponent implements OnInit {

  curso: Curso;
  autoCompleteControl= new FormControl();
  examenesFiltrados: Examen[]=[];
  examenesAsignar: Examen[]=[];
  examenes: Examen[]=[];
  dataSource: MatTableDataSource<Examen>;
  //importar el pagiandor
  @ViewChild(MatPaginator,{static: true}) paginator: MatPaginator;
  pageSizeOptions=[3,5,10,20,50];

  mostrarColumnas =['nombre','asignatura', 'eliminar'];

  mostrarColumnasExamenes=['id','nombre','asignaturas','eliminar'];

  tabIndex=0;

  constructor(private route: ActivatedRoute,
              private router: Router,
                private cursoService: CursoService,
                private examenService: ExamenService){

  }

  ngOnInit(): void {
    this.route.paramMap.subscribe(params=>{
      //convertir de get a number con el + 
      const id= +params.get('id');
      this.cursoService.ver(id).subscribe(c=> {
        this.curso=c;
        this.examenes= this.curso.examenes;
        this.iniciarPaginador();
      });
    });
    // es reactivo
    this.autoCompleteControl.valueChanges.pipe(
      //tratar el texto o el valor d lo que escojes del tipo examen
      map(valor =>typeof valor ==='string'? valor: valor.nombre),
      //retornar string del tipo observables de examene s cambair el flujo, buscamos en el back
      flatMap(valor => valor? this.examenService.filtrarPorNombre(valor): [])
    ).subscribe( examenes => this.examenesFiltrados= examenes);
  }

  private iniciarPaginador(){
    this.dataSource= new MatTableDataSource<Examen>(this.examenes);
    this.dataSource.paginator= this.paginator;
     this.paginator._intl.itemsPerPageLabel = 'Registros por página';
 }

  //puede ser indefinido{
  mostrarNombre(examen?: Examen) : string{
    return examen? examen.nombre:'';
  } 

  selecionarExamen(event: MatAutocompleteSelectedEvent): void{
    const examen = event.option.value as Examen;
    //usar el arreglo para agregar examenes 
    //this.examenesAsignar.push(examen);

    if(! this.existe(examen.id)){
      this.examenesAsignar = this.examenesAsignar.concat(examen);
      console.log(this.examenesAsignar);
      
    }else{
      Swal.fire(
        'ERROR',
        ` El examen ${examen.nombre} ya esta asignado al curso`,
        'error'
      );
    }
    //limpiar el autocomplete
    this.autoCompleteControl.setValue('');
    event.option.deselect();
    //foco en el input
    event.option.focus();
  }

  //validar que no exista exmanes doble
  private existe(id:number):boolean{
    let existe=false;
    this.examenesAsignar.concat(this.examenes)
    .forEach(e => {
      if(id === e.id){
        existe=true;
      }
    });
    return existe;
  }

  eliminarDelAsignar(examen: Examen){
    this.examenesAsignar= this.examenesAsignar.filter( e=> examen.id !=e.id);
  }

  asignar(): void{
    console.log(this.examenesAsignar);
    this.cursoService.asignarExamenes(this.curso, this.examenesAsignar)
    .subscribe(curso =>{
      //actualziar la lsita de exmanes
      this.examenes= this.examenes.concat(this.examenesAsignar);
      this.iniciarPaginador();
      this.examenesAsignar=[]; //resetemaos 

      Swal.fire('ASIGNADOS:'
        , ` Examenes asignados  con exito al curson ${curso.nombre}`
        , 'success'
      );
    });

    this.tabIndex=2;    //camniamos d e pesteña
  }


eliminarExamenDelCurso(examen: Examen):void{

    Swal.fire({
          title: 'Cuidado:',
          text: `¿Seguro que desea eliminar a ${examen.nombre} ?`,
          icon: 'warning',
          showCancelButton: true,
          confirmButtonColor: '#3085d6',
          cancelButtonColor: '#d33',
          confirmButtonText: 'Si, eliminar!'
        }).then((result) => {
          if (result.value) {
            this.cursoService.eliminarExamen(this.curso, examen)
            .subscribe(curso => {
              //elimianr alumno q eliminamos
              this.examenes = this.examenes.filter(e => e.id !== examen.id);
              this.iniciarPaginador();
              Swal.fire(
                'Eliminado:',
                `Examen ${examen.nombre} eliminado con éxito del curso ${curso.nombre}.`,
                'success'
              );
            });    

          }
        });

}


}
