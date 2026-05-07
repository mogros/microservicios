import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router, ActivatedRoute } from '@angular/router';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent implements OnInit {

  loginForm!: FormGroup;
  loading   = false;
  error     = '';
  returnUrl = '/cursos';

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private router: Router,
    private route: ActivatedRoute
  ) {}

  ngOnInit(): void {
    if (this.authService.hasToken()) {
      this.router.navigate([this.returnUrl]);
      return;
    }
    this.returnUrl = this.route.snapshot.queryParams['returnUrl'] || '/cursos';
    this.loginForm = this.fb.group({
      username: ['', [Validators.required, Validators.minLength(3)]],
      password: ['', [Validators.required, Validators.minLength(8)]]
    });
  }

  get username() { return this.loginForm.get('username')!; }
  get password() { return this.loginForm.get('password')!; }

  onSubmit(): void {
    if (this.loginForm.invalid) return;
    this.loading = true;
    this.error   = '';

    this.authService.login(this.loginForm.value).subscribe({
      next: () => this.router.navigate([this.returnUrl]),
      error: err => {
        this.loading = false;
        console.error('Error de login completo:', err);

        if (err.status === 0) {
          // Sin respuesta del servidor — CORS o servidor caido
          this.error = 'No se pudo conectar al servidor. Verifica que el Gateway esté corriendo en localhost:8090.';
        } else if (err.status === 401) {
          this.error = 'Usuario o contraseña incorrectos.';
        } else if (err.status === 403) {
          this.error = 'Acceso denegado (403). Revisa la configuración CORS del Gateway.';
        } else if (err.status === 404) {
          this.error = 'Ruta no encontrada (404). Verifica que ms-auth esté corriendo.';
        } else {
          this.error = `Error ${err.status}: ${err.error?.message || err.message || 'Error desconocido'}`;
        }
      }
    });
  }
}
