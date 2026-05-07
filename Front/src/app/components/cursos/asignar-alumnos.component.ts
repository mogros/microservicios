import { Component, OnInit, ViewChild } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { MatTableDataSource } from '@angular/material/table';
import { MatPaginator } from '@angular/material/paginator';
import { SelectionModel } from '@angular/cdk/collections';
import Swal from 'sweetalert2';
import { Curso } from '../../models/curso';
import { Alumno } from '../../models/alumno';
import { CursoService } from '../../services/curso.service';
import { AlumnoService } from '../../services/alumno.service';

@Component({
  selector: 'app-asignar-alumnos',
  templateUrl: './asignar-alumnos.component.html',
  styleUrl: './asignar-alumnos.component.css'
})
export class AsignarAlumnosComponent implements OnInit {

  curso: Curso;
  alumnosAsignar: Alumno[] = [];
  alumnos: Alumno[] = [];
  mostrarColumnas: string[] = ['nombre', 'apellido', 'seleccion'];
  mostrarColumnasAlumnos: string[] = ['id', 'nombre', 'apellido', 'email', 'eliminar'];
  seleccion = new SelectionModel<Alumno>(true, []);

  // Empieza en pestaña ALUMNOS (index 1) para mostrar los ya asignados al abrir
  tabIndex = 1;

  dataSource: MatTableDataSource<Alumno>;
  @ViewChild(MatPaginator, { static: true }) paginator: MatPaginator;
  pageSizeOptions = [3, 5, 10, 20, 50];

  constructor(
    private route: ActivatedRoute,
    private cursoService: CursoService,
    private alumnoService: AlumnoService
  ) {}

  ngOnInit(): void {
    this.route.paramMap.subscribe(params => {
      const id: number = +params.get('id');
      this.cursoService.ver(id).subscribe(c => {
        this.curso = c;
        this.alumnos = this.curso.alumnos ?? [];
        this.iniciarPaginador();
      });
    });
  }

  private iniciarPaginador(): void {
    this.dataSource = new MatTableDataSource<Alumno>(this.alumnos);
    this.dataSource.paginator = this.paginator;
    this.paginator._intl.itemsPerPageLabel = 'Registros por página';
  }

  filtrar(nombre: string): void {
    nombre = nombre ? nombre.trim() : '';
    if (nombre !== '') {
      this.alumnoService.filtrarPorNombre(nombre).subscribe(alumnos =>
        this.alumnosAsignar = alumnos.filter(a =>
          !this.alumnos.some(ca => ca.id === a.id)
        )
      );
    }
  }

  estanTodosSeleccionados(): boolean {
    return this.seleccion.selected.length === this.alumnosAsignar.length;
  }

  seleccionarDesseleccionarTodos(): void {
    this.estanTodosSeleccionados()
      ? this.seleccion.clear()
      : this.alumnosAsignar.forEach(a => this.seleccion.select(a));
  }

  asignar(): void {
    this.cursoService.asignarAlumnos(this.curso, this.seleccion.selected).subscribe({
      next: () => {
        this.tabIndex = 1;
        this.alumnos = this.alumnos.concat(this.seleccion.selected);
        this.iniciarPaginador();
        this.alumnosAsignar = [];
        this.seleccion.clear();
        Swal.fire('Asignados', `Alumnos asignados al curso ${this.curso.nombre}`, 'success');
      },
      error: e => {
        if (e.status === 500 && e.error?.message?.indexOf('constraint') > -1) {
          Swal.fire('Cuidado', 'El alumno ya está asociado a otro curso.', 'error');
        }
      }
    });
  }

  eliminarAlumno(alumno: Alumno): void {
    Swal.fire({
      title: 'Cuidado',
      text: `¿Eliminar a ${alumno.nombre} del curso?`,
      icon: 'warning',
      showCancelButton: true,
      confirmButtonText: 'Sí, eliminar',
      cancelButtonText: 'Cancelar'
    }).then(result => {
      if (result.isConfirmed) {
        this.cursoService.eliminarAlumno(this.curso, alumno).subscribe(curso => {
          this.alumnos = this.alumnos.filter(a => a.id !== alumno.id);
          this.iniciarPaginador();
          Swal.fire('Eliminado', `${alumno.nombre} eliminado del curso ${curso.nombre}`, 'success');
        });
      }
    });
  }
}
