import { Component, OnInit, ViewChild } from '@angular/core';
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
import { Respuesta, ResumenIntentos } from '../../models/respuesta';
import Swal from 'sweetalert2';
import { VerExamenModalComponent } from './ver-examen-modal.component';

interface ExamenConIntentos extends Examen {
    totalIntentos?: number;
}

@Component({
  selector: 'app-responder-examen',
  templateUrl: './responder-examen.component.html',
  styleUrl: './responder-examen.component.css'
})
export class ResponderExamenComponent implements OnInit {

  alumno: Alumno;
  curso: Curso;
  examenes: ExamenConIntentos[] = [];

  mostrarColumnasExamenes = ['id','nombre','asignaturas','preguntas','intentos','responder','ver'];

  dataSource: MatTableDataSource<ExamenConIntentos>;
  @ViewChild(MatPaginator, { static: true }) paginator: MatPaginator;

  pageSizeOptions = [3, 5, 10, 20];

  constructor(
    private route: ActivatedRoute,
    private alumnoService: AlumnoService,
    private cursoService: CursoService,
    private respuestaService: RespuestaService,
    public dialog: MatDialog
  ) {}

  ngOnInit(): void {
    this.route.paramMap.subscribe(params => {
      const id = +params.get('id');
      this.alumnoService.ver(id).subscribe(alumno => {
        this.alumno = alumno;
        this.cursoService.obtenerCursoPorAlumnoId(this.alumno).subscribe(curso => {
          this.curso = curso;
          this.examenes = (curso?.examenes ?? []) as ExamenConIntentos[];
          // Cargar número de intentos para cada examen
          this.cargarIntentos();
          this.dataSource = new MatTableDataSource<ExamenConIntentos>(this.examenes);
          this.dataSource.paginator = this.paginator;
          this.paginator._intl.itemsPerPageLabel = 'Registros por página: ';
        });
      });
    });
  }

  /** Carga el resumen de intentos de cada examen para el alumno actual */
  private cargarIntentos(): void {
    this.examenes.forEach(examen => {
      this.respuestaService.obtenerResumenIntentos(this.alumno, examen).subscribe({
        next: resumen => {
          examen.totalIntentos = resumen.totalIntentos;
          // Marcar como respondido si tiene al menos un intento
          if (resumen.totalIntentos > 0) {
            examen.respondido = true;
          }
        },
        error: () => {
          examen.totalIntentos = 0;
        }
      });
    });
  }

  responderExamen(examen: ExamenConIntentos): void {
    const modalRef = this.dialog.open(ResponderExamenModalComponent, {
      width: '750px',
      data: { curso: this.curso, alumno: this.alumno, examen: examen }
    });

    modalRef.afterClosed().subscribe((respuestasMap: Map<number, Respuesta>) => {
      if (respuestasMap) {
        const respuestas: Respuesta[] = Array.from(respuestasMap.values());
        this.respuestaService.crear(respuestas).subscribe(rs => {
          examen.respondido = true;
          examen.totalIntentos = (examen.totalIntentos ?? 0) + 1;
          Swal.fire({
            title: 'ENVIADAS',
            text: `Intento ${examen.totalIntentos} guardado correctamente`,
            icon: 'success'
          });
        });
      }
    });
  }

  verExamen(examen: ExamenConIntentos): void {
    // Si tiene múltiples intentos, preguntar cuál ver
    if ((examen.totalIntentos ?? 0) > 1) {
      this.seleccionarIntento(examen);
    } else {
      this.verIntento(examen, 1);
    }
  }

  private seleccionarIntento(examen: ExamenConIntentos): void {
    const opciones = Array.from({ length: examen.totalIntentos }, (_, i) => i + 1)
      .map(n => `<option value="${n}">Intento ${n}</option>`)
      .join('');

    Swal.fire({
      title: 'Selecciona un intento',
      html: `<select id="swal-intento" class="swal2-select">${opciones}</select>`,
      confirmButtonText: 'Ver',
      showCancelButton: true,
      cancelButtonText: 'Cancelar',
      preConfirm: () => {
        const sel = document.getElementById('swal-intento') as HTMLSelectElement;
        return parseInt(sel.value, 10);
      }
    }).then(result => {
      if (result.isConfirmed && result.value) {
        this.verIntento(examen, result.value);
      }
    });
  }

  private verIntento(examen: ExamenConIntentos, numeroIntento: number): void {
    this.respuestaService.obtenerRespuestasPorIntento(this.alumno, examen, numeroIntento)
      .subscribe(rs => {
        this.dialog.open(VerExamenModalComponent, {
          width: '800px',
          data: {
            curso: this.curso,
            examen: examen,
            respuestas: rs,
            numeroIntento: numeroIntento,
            totalIntentos: examen.totalIntentos
          }
        });
      });
  }
}
