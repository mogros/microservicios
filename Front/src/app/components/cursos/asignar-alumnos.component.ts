import { ActivatedRoute } from '@angular/router';
import { Curso } from './../../models/curso';
import { Component, OnInit, ViewChild } from '@angular/core';
import { CursoService } from '../../services/curso.service';
import { AlumnoService } from '../../services/alumno.service';
import { Alumno } from '../../models/alumno';
import { SelectionModel } from '@angular/cdk/collections';
import Swal from 'sweetalert2';
import { MatTableDataSource } from '@angular/material/table';
import { MatPaginator } from '@angular/material/paginator';

@Component({
  selector: 'app-asignar-alumnos',
  templateUrl: './asignar-alumnos.component.html',
  styleUrl: './asignar-alumnos.component.css'
})
export class AsignarAlumnosComponent implements OnInit {

  curso: Curso;
  alumnosAsignar: Alumno[]=[];
  alumnos: Alumno[]=[];
  //para mostara columnas nen la tabla
  mostrarColumnas: string[]=['nombre','apellido','seleccion'];
  mostrarColumnasAlumnos: string[]=['id','nombre','apellido','email', 'eliminar'];
  //para seleccioanr muchso
  seleccion: SelectionModel<Alumno>= new SelectionModel<Alumno>(true,[]);
  tabIndex=0;

  //paginar de manera distinata
  dataSource: MatTableDataSource<Alumno>;
  @ViewChild(MatPaginator, {static: true}) paginator: MatPaginator;
  pageSizeOptions: number[] = [3, 5, 10, 20, 50];

  constructor(private route: ActivatedRoute,
              private cursoService: CursoService,
              private alumnoService: AlumnoService){
  }

  ngOnInit(){
    this.route.paramMap.subscribe(params=>{
        //convertir a enetro
        const id: number=+params.get('id');
        //usacr el curso al back
        this.cursoService.ver(id).subscribe(c=> {
          this.curso=c;
          this.alumnos=this.curso.alumnos;
          this.iniciarPaginador();
        }) ;
    });
  }

  private iniciarPaginador(): void{
    //crear instancia de datasource
    this.dataSource = new MatTableDataSource<Alumno>(this.alumnos);
    //pasr el paginador
    this.dataSource.paginator = this.paginator;
    this.paginator._intl.itemsPerPageLabel = 'Registros por página';
  }


  filtrar(nombre: string): void{
    //preguntamos si no etsa vacio y quitas espacios en blanco
    nombre=nombre !== undefined ? nombre.trim():'';
    if (nombre!== ''){
      this.alumnoService.filtrarPorNombre(nombre)
      .subscribe(alumnos=> this.alumnosAsignar=alumnos.filter(a => {
        // filter envia bolleano
        let filtrar =true;
        this.alumnos.forEach(ca =>{
          if(a.id === ca.id){
            filtrar=false;    // para omitir  alumnos en el curso
          }
        });
        return filtrar;
      })) ;
    }
  }
  
  estanTodosSeleccionados(): boolean{
    const seleccionados = this.seleccion.selected.length;
    const numAlumnos= this.alumnosAsignar.length;
    return(seleccionados===numAlumnos);
  }

  seleccionarDesseleccionarTodos(): void{
    this.estanTodosSeleccionados()?
      this.seleccion.clear():
      //por cada alumno seleccionamos
      this.alumnosAsignar.forEach(a=> this.seleccion.select(a));
  }

  asignar(): void{
    //ver alumnos seleccionados
    console.log(this.seleccion.selected);
    this.cursoService.asignarAlumnos(this.curso, this.seleccion.selected)
    .subscribe(c => {
        this.tabIndex=2;
        Swal.fire('Asignados: ', `Alumnos asigandos con exito al curso ${this.curso.nombre}`,
          'success'
        );
        //concatenar a la lisa d alumnos los alumnos nuevos seleciionados
        this.alumnos= this.alumnos.concat(this.seleccion.selected);
        this.iniciarPaginador();
        this.alumnosAsignar=[];
        this.seleccion.clear();
        }

         ,

       
        e => { //manejo de error       
        if(e.status === 500){          
          const mensaje = e.error.message as string;
          if(mensaje.indexOf('constraint') > -1){		//si mensaje contiene
            Swal.fire(
              'Cuidado:',
              'No se puede asignar el alumno ya está asociado a otro curso.',
              'error'
            );
          }
          
        }
      }
    );
  }

   eliminarAlumno(alumno: Alumno): void {
    Swal.fire({
      title: 'Cuidado:',
      text: `¿Seguro que desea eliminar a ${alumno.nombre} ?`,
      icon: 'warning',
      showCancelButton: true,
      confirmButtonColor: '#3085d6',
      cancelButtonColor: '#d33',
      confirmButtonText: 'Si, eliminar!'
    }).then((result) => {
      if (result.value) {
        this.cursoService.eliminarAlumno(this.curso, alumno)
        .subscribe(curso => {
          //elimianr alumno q eliminamos
          this.alumnos = this.alumnos.filter(a => a.id !== alumno.id);
          this.iniciarPaginador();
          //this.iniciarPaginador();
          Swal.fire(
            'Eliminado:',
            `Alumno ${alumno.nombre} eliminado con éxito del curso ${curso.nombre}.`,
            'success'
          );
        });    

      }
    });
  }

}
