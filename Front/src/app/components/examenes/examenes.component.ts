import { Component, OnInit } from '@angular/core';
import { CommonListarComponent } from '../common-listar.components';
import { Examen } from '../../models/examen';
import { ExamenService } from '../../services/examen.service';

@Component({
  selector: 'app-examenes',
  templateUrl: './examenes.component.html',
  styleUrl: './examenes.component.css'
})
export class ExamenesComponent extends CommonListarComponent<Examen, ExamenService> implements OnInit{

  
       constructor( service: ExamenService){
         super(service);
         this.titulo='Listado Examenes';
         this.nombreModel= Examen.name;
       }


}
