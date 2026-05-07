import { Component, OnInit, ViewChild } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { FormControl } from '@angular/forms';
import { MatTableDataSource } from '@angular/material/table';
import { MatPaginator } from '@angular/material/paginator';
import { MatAutocompleteSelectedEvent } from '@angular/material/autocomplete';
import { map, flatMap } from 'rxjs/operators';
import Swal from 'sweetalert2';
import { Curso } from '../../models/curso';
import { Examen } from '../../models/examen';
import { CursoService } from '../../services/curso.service';
import { ExamenService } from '../../services/examen.service';

@Component({
  selector: 'app-asignar-examenes',
  templateUrl: './asignar-examenes.component.html',
  styleUrl: './asignar-examenes.component.css'
})
export class AsignarExamenesComponent implements OnInit {

  curso: Curso;
  autoCompleteControl = new FormControl();
  examenesFiltrados: Examen[] = [];
  examenesAsignar: Examen[] = [];
  examenes: Examen[] = [];

  dataSource: MatTableDataSource<Examen>;
  @ViewChild(MatPaginator, { static: true }) paginator: MatPaginator;
  pageSizeOptions = [3, 5, 10, 20, 50];

  mostrarColumnas = ['nombre', 'asignatura', 'eliminar'];
  mostrarColumnasExamenes = ['id', 'nombre', 'asignaturas', 'eliminar'];

  // Empieza en pestaña EXAMENES (index 1) para mostrar los ya asignados al abrir
  tabIndex = 1;

  constructor(
    private route: ActivatedRoute,
    private cursoService: CursoService,
    private examenService: ExamenService
  ) {}

  ngOnInit(): void {
    this.route.paramMap.subscribe(params => {
      const id = +params.get('id');
      this.cursoService.ver(id).subscribe(c => {
        this.curso = c;
        this.examenes = this.curso.examenes ?? [];
        this.iniciarPaginador();
      });
    });

    // Autocomplete reactivo
    this.autoCompleteControl.valueChanges.pipe(
      map(valor => typeof valor === 'string' ? valor : valor.nombre),
      flatMap(valor => valor ? this.examenService.filtrarPorNombre(valor) : [])
    ).subscribe(examenes => this.examenesFiltrados = examenes);
  }

  private iniciarPaginador(): void {
    this.dataSource = new MatTableDataSource<Examen>(this.examenes);
    this.dataSource.paginator = this.paginator;
    this.paginator._intl.itemsPerPageLabel = 'Registros por página';
  }

  mostrarNombre(examen?: Examen): string {
    return examen ? examen.nombre : '';
  }

  selecionarExamen(event: MatAutocompleteSelectedEvent): void {
    const examen = event.option.value as Examen;
    if (!this.existe(examen.id)) {
      this.examenesAsignar = this.examenesAsignar.concat(examen);
    } else {
      Swal.fire('Error', `El examen ${examen.nombre} ya está asignado al curso`, 'error');
    }
    this.autoCompleteControl.setValue('');
    event.option.deselect();
    event.option.focus();
  }

  private existe(id: number): boolean {
    return this.examenesAsignar.concat(this.examenes).some(e => e.id === id);
  }

  eliminarDelAsignar(examen: Examen): void {
    this.examenesAsignar = this.examenesAsignar.filter(e => e.id !== examen.id);
  }

  asignar(): void {
    this.cursoService.asignarExamenes(this.curso, this.examenesAsignar).subscribe(curso => {
      this.examenes = this.examenes.concat(this.examenesAsignar);
      this.iniciarPaginador();
      this.examenesAsignar = [];
      this.tabIndex = 1;
      Swal.fire('Asignados', `Exámenes asignados al curso ${curso.nombre}`, 'success');
    });
  }

  eliminarExamenDelCurso(examen: Examen): void {
    Swal.fire({
      title: 'Cuidado',
      text: `¿Eliminar ${examen.nombre} del curso?`,
      icon: 'warning',
      showCancelButton: true,
      confirmButtonText: 'Sí, eliminar',
      cancelButtonText: 'Cancelar'
    }).then(result => {
      if (result.isConfirmed) {
        this.cursoService.eliminarExamen(this.curso, examen).subscribe(curso => {
          this.examenes = this.examenes.filter(e => e.id !== examen.id);
          this.iniciarPaginador();
          Swal.fire('Eliminado', `${examen.nombre} eliminado del curso ${curso.nombre}`, 'success');
        });
      }
    });
  }
}
