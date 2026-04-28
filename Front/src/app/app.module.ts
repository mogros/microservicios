import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';


import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { AlumnosComponent } from './components/alumnos/alumnos.component';
import { CursosComponent } from './components/cursos/cursos.component';
import { ExamenesComponent } from './components/examenes/examenes.component';
import { LayoutModule } from './layout/layout.module';
import { HttpClientModule } from '@angular/common/http';
import { AlumnosFormComponent } from './components/alumnos/alumnos-form.component';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import {MatPaginatorModule} from '@angular/material/paginator';
import { MatSelectModule } from '@angular/material/select'; // 👈 Agrega esta línea
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { CursoFormComponent } from './components/cursos/curso-form.component';
import { ExamenFormComponent } from './components/examenes/examen-form.component'; // 👈 También necesario para Angular Material
import {MatDialogModule} from '@angular/material/dialog';
import {MatExpansionModule} from '@angular/material/expansion';


import {MatTableModule} from '@angular/material/table';
import {MatInputModule} from '@angular/material/input';
import {MatCheckboxModule} from '@angular/material/checkbox';
import {MatButtonModule} from '@angular/material/button';
import { AsignarAlumnosComponent } from './components/cursos/asignar-alumnos.component';
import {MatCardModule} from '@angular/material/card';
import {MatTabsModule} from '@angular/material/tabs';
import {MatAutocompleteModule} from '@angular/material/autocomplete';
import { AsignarExamenesComponent } from './components/cursos/asignar-examenes.component';
import { CdkOverlayOrigin } from "@angular/cdk/overlay";
import { ResponderExamenComponent } from './components/alumnos/responder-examen.component';
import { ResponderExamenModalComponent } from './components/alumnos/responder-examen-modal.component';
import { VerExamenModalComponent } from './components/alumnos/ver-examen-modal.component';


//import { AsignarAlumnosComponent } from './components/alumnos/asignar-alumnos.component';





@NgModule({
  declarations: [
    AppComponent,
    AlumnosComponent,
    CursosComponent,
    ExamenesComponent,
    AlumnosFormComponent,
    CursoFormComponent,
    ExamenFormComponent,
    AsignarAlumnosComponent,
    AsignarExamenesComponent,
    ResponderExamenComponent,
    ResponderExamenModalComponent,
    VerExamenModalComponent
  ],
  //entryComponents:[ResponderExamenModalComponent],
  imports: [
    BrowserModule,
    AppRoutingModule,
    NgbModule,
    LayoutModule,
    HttpClientModule,
    FormsModule,
    MatPaginatorModule,
    MatSelectModule,
    BrowserAnimationsModule,
    MatTableModule,
    MatInputModule,
    MatCheckboxModule,
    MatButtonModule,
    MatCardModule,
    MatTabsModule,
    MatAutocompleteModule,
    ReactiveFormsModule,
    CdkOverlayOrigin,
    MatDialogModule,
    MatExpansionModule
],
  providers: [

  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
