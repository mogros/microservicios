import { Component, OnInit, ViewChild, viewChild } from '@angular/core';
import { Alumno } from '../../models/alumno';
import { Curso } from '../../models/curso';
import { Examen } from '../../models/examen';
import { ActivatedRoute } from '@angular/router';
import { AlumnoService } from '../../services/alumno.service';
import { CursoService } from '../../services/curso.service';
import { MatTableDataSource } from '@angular/material/table';
import { MatPaginator } from '@angular/material/paginator';
import { MatDialog } from '@angular/material/dialog';
import { ResponderExamenModalComponent } from './responder-examen-modal.component';
import { RespuestaService } from '../../services/respuesta.service';
import { Respuesta } from '../../models/respuesta';
import Swal from 'sweetalert2';
import { VerExamenModalComponent } from './ver-examen-modal.component';

@Component({
  selector: 'app-responder-examen',
  templateUrl: './responder-examen.component.html',
  styleUrl: './responder-examen.component.css'
})
export class ResponderExamenComponent implements OnInit {

  alumno:Alumno;
  curso: Curso;
  examenes: Examen[]=[];

  mostrarColumnasExamenes=['id','nombre','asignaturas','preguntas','responder','ver'];

  dataSource: MatTableDataSource<Examen>;
  @ViewChild(MatPaginator, {static: true}) paginator: MatPaginator;

  pageSizeOptions=[3,5,10,20,30,50];

  constructor(private route: ActivatedRoute,
    private alumnoService: AlumnoService,
    private cursoService: CursoService,
    private respuestaService: RespuestaService,
    public dialog: MatDialog  ){

  }

  ngOnInit(): void {
    this.route.paramMap.subscribe( params=>{
      const id=+params.get('id');  
      this.alumnoService.ver(id).subscribe(alumno=>{
        this.alumno=alumno;
        this.cursoService.obtenerCursoPorAlumnoId(this.alumno).subscribe(
          curso=>{
            this.curso=curso;
            this.examenes= (curso && curso.examenes)? curso.examenes:[];
            this.dataSource= new MatTableDataSource<Examen>(this.examenes);
            this.dataSource.paginator=this.paginator;
            //idioma
            this.paginator._intl.itemsPerPageLabel='Registros por pagina: ';
          }
        );
      });
    });
  }

  responderExamen(examen: Examen): void{
    const modalRef= this.dialog.open(ResponderExamenModalComponent,{
      width:'750px',
      data: {curso: this.curso, alumno: this.alumno, examen: examen}
    });
    //cuando se cierra el modal
    modalRef.afterClosed().subscribe((respuestasMap: Map<number, Respuesta>) =>{
      console.log('modal responder examen  ha sido enviado y cerrado');
      console.log(respuestasMap);
      if(respuestasMap){ //si existe se gurad en el back
        //convertimos los valroes dl mapa en un arreglo
        const respuestas: Respuesta[] = Array.from(respuestasMap.values());
        this.respuestaService.crear(respuestas).subscribe(rs=>{
          examen.respondido=true;
          Swal.fire(
            'ENVIADAS',
            'Preguntas enviadas con exito',
            'success'
          );
          console.log(rs);
        });
      }

    });
  }

  verExamen(examen: Examen): void{
    this.respuestaService.obtenerRespuestasPorAlumnoPorExamen(this.alumno, examen).subscribe(
      rs=>{
        const modalRef= this.dialog.open(VerExamenModalComponent, {width: '750px',
          data: {curso: this.curso, examen: examen, respuestas: rs}
        });
        modalRef.afterClosed().subscribe(()=>{
          console.log(' Modal ver examen cerrado');
        });//no se emeite nada, argumento vacio
      }
    );
  }

}
