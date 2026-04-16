import { Component, OnInit, ViewChild } from '@angular/core';
import Swal from 'sweetalert2'
import { MatPaginator, PageEvent } from '@angular/material/paginator';
import { Generic } from '../models/generic';
import { CommonService } from '../services/common.service';

@Component({
  template: '' // ← Template vacío para clases abstractas
})

export abstract class CommonListarComponent<E extends Generic, S extends CommonService<E>> implements OnInit {

  titulo: string;
  lista: E[];
  protected nombreModel: string;

  totalRegistros=0;
  paginaActual=0;
  totalPorPagina=4;
  pageSizeOptions: number[]=[3,5, 10, 25, 100];

  //cambir ingles a castellano
  @ViewChild(MatPaginator) paginator: MatPaginator;

  constructor(protected service: S){

  }

  ngOnInit() {
    //todos los metdos del back se deben implemnatr aqui 
    //es un observable y s etiene que suscribir
    //this.service.listar().subscribe( alumnos => this.alumnos= alumnos);
  
    this.calcularrRangos();
  }


  paginar(event: PageEvent): void{
      this.paginaActual = event.pageIndex;
      this.totalPorPagina = event.pageSize;
      this.calcularrRangos();
  }

  private calcularrRangos(){
    //const paginaActual = this.paginaActual+'';
    //const totalPorPagina = this.totalPorPagina+'';
    //this.service.listarPaginas(paginaActual, totalPorPagina).subscribe( p =>
 
    this.service.listarPaginas(this.paginaActual.toString(), this.totalPorPagina.toString()).subscribe( p =>
      {
        this.lista= p.content as E[];
        this.totalRegistros = p.totalElements as number;
        this.paginator._intl.itemsPerPageLabel='Registros';
      }
      );
  }

  public eliminar(e: E): void{

    Swal.fire({
      title: "CUIDADO: ",
      text: `¿Seguro de eliminar a ${e.nombre}?`,
      icon: "warning",
      showCancelButton: true,
      confirmButtonColor: "#3085d6",
      cancelButtonColor: "#d33",
      confirmButtonText: "SI, ELIMINAR"
    }).then((result) => {
      if (result.isConfirmed) {
        this.service.eliminar(e.id).subscribe(()=>{
        this.calcularrRangos();
        Swal.fire('ELIMINADO: ',`${this.nombreModel}} ${e.nombre} eliminado con exito` , 'success');
        });
      }
    });


  }
S
}
