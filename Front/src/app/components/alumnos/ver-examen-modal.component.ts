import { Component, Inject, OnInit } from '@angular/core';
import { Curso } from '../../models/curso';
import { Examen } from '../../models/examen';
import { Respuesta } from '../../models/respuesta';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';

@Component({
  selector: 'app-ver-examen-modal',
  templateUrl: './ver-examen-modal.component.html',
  styleUrl: './ver-examen-modal.component.css'
})
export class VerExamenModalComponent implements OnInit {

  curso: Curso;
  examen: Examen;
  respuestas: Respuesta[];
  numeroIntento: number;
  totalIntentos: number;
  fechaRespuesta: string;

  constructor(
    @Inject(MAT_DIALOG_DATA) public data: any,
    public modalRef: MatDialogRef<VerExamenModalComponent>
  ) {}

  ngOnInit(): void {
    this.curso          = this.data.curso   as Curso;
    this.examen         = this.data.examen  as Examen;
    this.respuestas     = this.data.respuestas as Respuesta[];
    this.numeroIntento  = this.data.numeroIntento ?? 1;
    this.totalIntentos  = this.data.totalIntentos ?? 1;
    // Tomar la fecha de la primera respuesta (todas del mismo intento tienen la misma)
    this.fechaRespuesta = this.respuestas?.[0]?.fechaRespuesta ?? null;
  }

  cerrar(): void {
    this.modalRef.close();
  }
}
