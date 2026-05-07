import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable, BehaviorSubject, tap } from 'rxjs';
import { BASE_ENDPOINT } from '../config/app';

export interface LoginRequest {
  username: string;
  password: string;
}

export interface RegisterRequest {
  username: string;
  email: string;
  password: string;
  roles?: string[];
}

export interface JwtResponse {
  token: string;
  type: string;
  id: number;
  username: string;
  email: string;
  roles: string[];
}

@Injectable({ providedIn: 'root' })
export class AuthService {

  private readonly AUTH_ENDPOINT = `${BASE_ENDPOINT}/auth`;
  private readonly TOKEN_KEY     = 'jwt_token';
  private readonly USER_KEY      = 'jwt_user';

  private loggedIn$ = new BehaviorSubject<boolean>(this.hasToken());
  public  isLoggedIn$ = this.loggedIn$.asObservable();
  private headers = new HttpHeaders({ 'Content-Type': 'application/json' });

  constructor(private http: HttpClient) {}

  login(request: LoginRequest): Observable<JwtResponse> {
    return this.http.post<JwtResponse>(
      `${this.AUTH_ENDPOINT}/login`, request, { headers: this.headers }
    ).pipe(tap(response => {
      sessionStorage.setItem(this.TOKEN_KEY, response.token);
      const { token, ...userWithoutToken } = response;
      sessionStorage.setItem(this.USER_KEY, JSON.stringify(userWithoutToken));
      this.loggedIn$.next(true);
    }));
  }

  registro(request: RegisterRequest): Observable<{ message: string }> {
    return this.http.post<{ message: string }>(
      `${this.AUTH_ENDPOINT}/registro`, request, { headers: this.headers }
    );
  }

  logout(): void {
    sessionStorage.removeItem(this.TOKEN_KEY);
    sessionStorage.removeItem(this.USER_KEY);
    this.loggedIn$.next(false);
  }

  getToken(): string | null {
    return sessionStorage.getItem(this.TOKEN_KEY);
  }

  hasToken(): boolean {
    const token = this.getToken();
    if (!token) return false;
    try {
      const payload = JSON.parse(atob(token.split('.')[1]));
      return Date.now() < payload.exp * 1000;
    } catch { return false; }
  }

  getUser(): any {
    const user = sessionStorage.getItem(this.USER_KEY);
    return user ? JSON.parse(user) : null;
  }

  getUsername(): string  { return this.getUser()?.username ?? ''; }
  getRoles(): string[]   { return this.getUser()?.roles ?? []; }
  hasRole(role: string): boolean { return this.getRoles().includes(role); }
  isAdmin(): boolean     { return this.hasRole('ROLE_ADMIN'); }
  isDocente(): boolean   { return this.hasRole('ROLE_DOCENTE') || this.isAdmin(); }
}
