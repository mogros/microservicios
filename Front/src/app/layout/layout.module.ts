import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { NavbarComponent } from './navbar/navbar.component';
import { AppRoutingModule } from '../app-routing.module';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';



@NgModule({
  declarations: [
    NavbarComponent
  ],
  exports:[NavbarComponent],
  imports: [
    CommonModule,
    NgbModule,
    AppRoutingModule
  ]
})
export class LayoutModule { }
