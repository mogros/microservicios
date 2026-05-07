import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { AlumnosComponent } from './components/alumnos/alumnos.component';
import { CursosComponent } from './components/cursos/cursos.component';
import { ExamenesComponent } from './components/examenes/examenes.component';
import { AlumnosFormComponent } from './components/alumnos/alumnos-form.component';
import { CursoFormComponent } from './components/cursos/curso-form.component';
import { ExamenFormComponent } from './components/examenes/examen-form.component';
import { AsignarAlumnosComponent } from './components/cursos/asignar-alumnos.component';
import { AsignarExamenesComponent } from './components/cursos/asignar-examenes.component';
import { ResponderExamenComponent } from './components/alumnos/responder-examen.component';
import { LoginComponent } from './components/auth/login.component';
import { ReportesComponent } from './components/reportes/reportes.component';
import { AuthGuard } from './guards/auth.guard';
import { RolGuard } from './guards/auth.guard';

const routes: Routes = [
  { path: 'login', component: LoginComponent },
  { path: '', pathMatch: 'full', redirectTo: 'cursos' },

  // Todos los autenticados
  { path: 'alumnos',                      component: AlumnosComponent,        canActivate: [AuthGuard] },
  { path: 'alumnos/form',                 component: AlumnosFormComponent,    canActivate: [AuthGuard] },
  { path: 'alumnos/form/:id',             component: AlumnosFormComponent,    canActivate: [AuthGuard] },
  { path: 'alumnos/responder-examen/:id', component: ResponderExamenComponent,canActivate: [AuthGuard] },
  { path: 'cursos',                       component: CursosComponent,         canActivate: [AuthGuard] },
  { path: 'examenes',                     component: ExamenesComponent,       canActivate: [AuthGuard] },

  // Solo admin y docentes
  { path: 'cursos/form',                 component: CursoFormComponent,       canActivate: [AuthGuard, RolGuard], data: { roles: ['ROLE_ADMIN','ROLE_DOCENTE'] } },
  { path: 'cursos/form/:id',             component: CursoFormComponent,       canActivate: [AuthGuard, RolGuard], data: { roles: ['ROLE_ADMIN','ROLE_DOCENTE'] } },
  { path: 'cursos/asignar-alumnos/:id',  component: AsignarAlumnosComponent,  canActivate: [AuthGuard, RolGuard], data: { roles: ['ROLE_ADMIN','ROLE_DOCENTE'] } },
  { path: 'cursos/asignar-examenes/:id', component: AsignarExamenesComponent, canActivate: [AuthGuard, RolGuard], data: { roles: ['ROLE_ADMIN','ROLE_DOCENTE'] } },
  { path: 'examenes/form',               component: ExamenFormComponent,      canActivate: [AuthGuard, RolGuard], data: { roles: ['ROLE_ADMIN','ROLE_DOCENTE'] } },
  { path: 'examenes/form/:id',           component: ExamenFormComponent,      canActivate: [AuthGuard, RolGuard], data: { roles: ['ROLE_ADMIN','ROLE_DOCENTE'] } },

  // Reportes — solo admin y docentes
  { path: 'reportes', component: ReportesComponent, canActivate: [AuthGuard, RolGuard], data: { roles: ['ROLE_ADMIN','ROLE_DOCENTE'] } },

  { path: '**', redirectTo: 'cursos' }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
