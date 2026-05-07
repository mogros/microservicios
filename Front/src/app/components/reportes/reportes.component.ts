import { Component, OnInit } from '@angular/core';
import { ChartConfiguration, ChartData, ChartType } from 'chart.js';
import * as XLSX from 'xlsx';
import { saveAs } from 'file-saver';
import {
  ReporteService, ResumenGeneral, AlumnoPorCurso,
  ParticipacionExamen, ActividadAlumno, ResumenAsignatura
} from '../../services/reporte.service';

@Component({
  selector: 'app-reportes',
  templateUrl: './reportes.component.html',
  styleUrls: ['./reportes.component.css']
})
export class ReportesComponent implements OnInit {

  // Estado general
  tabActiva = 0;
  cargando = false;

  // Datos de los reportes
  resumen: ResumenGeneral;
  alumnosPorCurso: AlumnoPorCurso[] = [];
  participacion: ParticipacionExamen[] = [];
  actividad: ActividadAlumno[] = [];
  asignaturas: ResumenAsignatura[] = [];

  // ── Gráfico 1: Alumnos por curso (barras) ─────────────────────
  barChartType: ChartType = 'bar';
  barChartData: ChartData<'bar'> = { labels: [], datasets: [] };
  barChartOptions: ChartConfiguration['options'] = {
    responsive: true,
    plugins: {
      legend: { display: true },
      title: { display: true, text: 'Alumnos y participación por curso' }
    },
    scales: { y: { beginAtZero: true, max: 100 } }
  };

  // ── Gráfico 2: Participación por examen (horizontal bar) ───────
  hBarChartType: ChartType = 'bar';
  hBarChartData: ChartData<'bar'> = { labels: [], datasets: [] };
  hBarChartOptions: ChartConfiguration['options'] = {
    indexAxis: 'y',
    responsive: true,
    plugins: {
      legend: { display: true },
      title: { display: true, text: '% Participación por examen' }
    },
    scales: { x: { beginAtZero: true, max: 100 } }
  };

  // ── Gráfico 3: Estado de alumnos (dona) ───────────────────────
  donaType: ChartType = 'doughnut';
  donaData: ChartData<'doughnut'> = { labels: [], datasets: [] };
  donaOptions: ChartConfiguration['options'] = {
    responsive: true,
    plugins: {
      legend: { position: 'bottom' },
      title: { display: true, text: 'Estado de alumnos' }
    }
  };

  // ── Gráfico 4: Exámenes por asignatura (pastel) ───────────────
  pieType: ChartType = 'pie';
  pieData: ChartData<'pie'> = { labels: [], datasets: [] };
  pieOptions: ChartConfiguration['options'] = {
    responsive: true,
    plugins: {
      legend: { position: 'right' },
      title: { display: true, text: 'Exámenes por asignatura' }
    }
  };

  // ── Gráfico 5: Progreso individual alumnos (línea) ────────────
  lineType: ChartType = 'line';
  lineData: ChartData<'line'> = { labels: [], datasets: [] };
  lineOptions: ChartConfiguration['options'] = {
    responsive: true,
    plugins: {
      legend: { display: false },
      title: { display: true, text: 'Progreso de alumnos (% completado)' }
    },
    scales: { y: { beginAtZero: true, max: 100 } }
  };

  // Colores
  private colores = [
    '#4e79a7','#f28e2b','#e15759','#76b7b2','#59a14f',
    '#edc948','#b07aa1','#ff9da7','#9c755f','#bab0ac'
  ];

  constructor(private reporteService: ReporteService) {}

  ngOnInit(): void {
    this.cargarTodo();
  }

  cargarTodo(): void {
    this.cargando = true;
    this.cargarResumen();
    this.cargarAlumnosPorCurso();
    this.cargarParticipacion();
    this.cargarActividad();
    this.cargarAsignaturas();
  }

  private cargarResumen(): void {
    this.reporteService.resumenGeneral().subscribe(r => this.resumen = r);
  }

  private cargarAlumnosPorCurso(): void {
    this.reporteService.alumnosPorCurso().subscribe(data => {
      this.alumnosPorCurso = data;
      this.barChartData = {
        labels: data.map(d => d.curso),
        datasets: [
          {
            label: 'Total alumnos',
            data: data.map(d => d.totalAlumnos),
            backgroundColor: '#4e79a7'
          },
          {
            label: '% Participación',
            data: data.map(d => d.porcentajeParticipacion),
            backgroundColor: '#f28e2b'
          }
        ]
      };
    });
  }

  private cargarParticipacion(): void {
    this.reporteService.participacionPorExamen().subscribe(data => {
      this.participacion = data;
      this.hBarChartData = {
        labels: data.map(d => d.examen),
        datasets: [{
          label: '% Alumnos que respondieron',
          data: data.map(d => d.porcentajeRespuesta),
          backgroundColor: data.map((_, i) => this.colores[i % this.colores.length])
        }]
      };
      this.cargando = false;
    });
  }

  private cargarActividad(): void {
    this.reporteService.actividadPorAlumno().subscribe(data => {
      this.actividad = data;

      // Dona: aprobados vs en progreso
      const aprobados = data.filter(a => a.estado === 'Aprobado').length;
      const enProgreso = data.filter(a => a.estado === 'En progreso').length;
      this.donaData = {
        labels: ['Aprobados (≥60%)', 'En progreso (<60%)'],
        datasets: [{
          data: [aprobados, enProgreso],
          backgroundColor: ['#59a14f', '#e15759']
        }]
      };

      // Línea: progreso individual (top 10)
      const top10 = data.slice(0, 10);
      this.lineData = {
        labels: top10.map(a => a.nombre.split(' ')[0]),
        datasets: [{
          label: '% Completado',
          data: top10.map(a => a.porcentajeCompletado),
          borderColor: '#4e79a7',
          backgroundColor: 'rgba(78,121,167,0.15)',
          fill: true,
          tension: 0.3,
          pointBackgroundColor: top10.map(a =>
            a.porcentajeCompletado >= 60 ? '#59a14f' : '#e15759')
        }]
      };
    });
  }

  private cargarAsignaturas(): void {
    this.reporteService.resumenPorAsignatura().subscribe(data => {
      this.asignaturas = data;
      this.pieData = {
        labels: data.map(d => d.asignatura),
        datasets: [{
          data: data.map(d => d.totalExamenes),
          backgroundColor: data.map((_, i) => this.colores[i % this.colores.length])
        }]
      };
    });
  }

  // ── Exportación Excel ─────────────────────────────────────────

  exportarAlumnosPorCurso(): void {
    const rows = this.alumnosPorCurso.map(d => ({
      'Curso': d.curso,
      'Total alumnos': d.totalAlumnos,
      'Total exámenes': d.totalExamenes,
      'Alumnos participaron': d.alumnosParticiparon,
      '% Participación': d.porcentajeParticipacion
    }));
    this.descargarExcel(rows, 'Alumnos_por_Curso');
  }

  exportarParticipacion(): void {
    const rows = this.participacion.map(d => ({
      'Examen': d.examen,
      'Curso': d.curso,
      'Total alumnos curso': d.totalAlumnosCurso,
      'Alumnos respondieron': d.alumnosRespondieron,
      '% Respuesta': d.porcentajeRespuesta
    }));
    this.descargarExcel(rows, 'Participacion_por_Examen');
  }

  exportarActividad(): void {
    const rows = this.actividad.map(d => ({
      'Alumno': d.nombre,
      'Email': d.email,
      'Cursos matriculado': d.cursosMatriculado,
      'Exámenes disponibles': d.examenesDisponibles,
      'Exámenes respondidos': d.examenesRespondidos,
      '% Completado': d.porcentajeCompletado,
      'Estado': d.estado
    }));
    this.descargarExcel(rows, 'Actividad_Alumnos');
  }

  exportarAsignaturas(): void {
    const rows = this.asignaturas.map(d => ({
      'Asignatura': d.asignatura,
      'Total exámenes': d.totalExamenes,
      'Total alumnos (suma cursos)': d.totalAlumnos,
      'Cursos involucrados': d.cursosInvolucrados.join(', ')
    }));
    this.descargarExcel(rows, 'Examenes_por_Asignatura');
  }

  private descargarExcel(rows: any[], nombre: string): void {
    const ws = XLSX.utils.json_to_sheet(rows);
    const wb = XLSX.utils.book_new();
    XLSX.utils.book_append_sheet(wb, ws, nombre.substring(0, 31));

    // Ajustar anchos de columna automáticamente
    const cols = Object.keys(rows[0] || {}).map(k => ({
      wch: Math.max(k.length, ...rows.map(r => String(r[k]).length)) + 2
    }));
    ws['!cols'] = cols;

    const excelBuffer = XLSX.write(wb, { bookType: 'xlsx', type: 'array' });
    const blob = new Blob([excelBuffer], { type: 'application/octet-stream' });
    saveAs(blob, `${nombre}_${new Date().toISOString().slice(0,10)}.xlsx`);
  }
}
