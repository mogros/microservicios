import { Alumno } from './../../models/alumno';
import { AlumnoService } from './../../services/alumno.service';
import { Component, OnInit, ViewChild } from '@angular/core';
import { CommonListarComponent } from '../common-listar.components';
import { BASE_ENDPOINT } from '../../config/app';

@Component({
  selector: 'app-alumnos',
  templateUrl: './alumnos.component.html',
  styleUrls: ['./alumnos.component.css']
})
export class AlumnosComponent  extends CommonListarComponent<Alumno, AlumnoService> implements OnInit {

  baseEndPoint= BASE_ENDPOINT+ '/alumnos';

  constructor(service: AlumnoService) {
    super(service);
    this.titulo = 'Listado de Alumnos';
    this.nombreModel = Alumno.name;
  }


}
