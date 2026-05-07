import { Component, Inject, OnInit, ViewChild } from '@angular/core';
import Swal from 'sweetalert2';
import { MatPaginator, PageEvent } from '@angular/material/paginator';
import { Generic } from '../models/generic';
import { CommonService } from '../services/common.service';

@Component({
  template: ''
})
export abstract class CommonListarComponent<E extends Generic, S extends CommonService<E>> implements OnInit {

  titulo: string;
  lista: E[];
  protected nombreModel: string;

  totalRegistros = 0;
  paginaActual = 0;
  totalPorPagina = 4;
  pageSizeOptions: number[] = [3, 5, 10, 25, 100];

  @ViewChild(MatPaginator) paginator: MatPaginator;

  // El @Inject(CommonService) es necesario para que Angular pueda resolver
  // el token de inyección cuando el tipo es un genérico abstracto.
  // Las clases concretas (AlumnosComponent, CursosComponent) pasan su
  // servicio concreto al super(service) y Angular lo resuelve correctamente.
  constructor(@Inject(CommonService) protected service: S) {}

  ngOnInit() {
    this.calcularrRangos();
  }

  paginar(event: PageEvent): void {
    this.paginaActual = event.pageIndex;
    this.totalPorPagina = event.pageSize;
    this.calcularrRangos();
  }

  private calcularrRangos(): void {
    this.service.listarPaginas(
      this.paginaActual.toString(),
      this.totalPorPagina.toString()
    ).subscribe(p => {
      this.lista = p.content as E[];
      this.totalRegistros = p.totalElements as number;
      this.paginator._intl.itemsPerPageLabel = 'Registros';
    });
  }

  public eliminar(e: E): void {
    Swal.fire({
      title: 'CUIDADO:',
      text: `¿Seguro de eliminar a ${e.nombre}?`,
      icon: 'warning',
      showCancelButton: true,
      confirmButtonColor: '#3085d6',
      cancelButtonColor: '#d33',
      confirmButtonText: 'SI, ELIMINAR',
      cancelButtonText: 'Cancelar'
    }).then(result => {
      if (result.isConfirmed) {
        this.service.eliminar(e.id).subscribe(() => {
          this.calcularrRangos();
          Swal.fire(
            'ELIMINADO',
            `${this.nombreModel} ${e.nombre} eliminado con éxito`,
            'success'
          );
        });
      }
    });
  }
}
