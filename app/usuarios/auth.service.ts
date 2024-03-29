import { HttpClient, HttpHeaders} from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, map } from 'rxjs';
import { Usuario } from './usuario';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private _token: string;
  private _username: string;
  private _role: string;

  constructor(private http: HttpClient) { }

  public get token(): string{
    if(this._token != null){
      return this._token;
    }else if(this._token == null && sessionStorage.getItem('token')){
      this._token = sessionStorage.getItem('token');
      return this._token;
    }else{
      return null;
    }
  }

  public get username(): string{
    if(this._username != null){
      return this._username;
    }else if(this._username == null && sessionStorage.getItem('username')){
      this._username = sessionStorage.getItem('username');
      return this._username;
    }else{
      return null;
    }
  }

  public get role(): string{
    if(this._role != null){
      return this._role;
    }else if(this._role == null && sessionStorage.getItem('role')){
      this._role = sessionStorage.getItem('role');
      return this._role;
    }else{
      return null;
    }
  }

  login(usuario: Usuario): Observable<any>{

    const apiUrl = 'http://localhost:9000/api/auth/login';
    const httpHeaders = new HttpHeaders({
      'Content-Type': 'application/json',
      'Authorization': 'No Auth'
    });

    return this.http.post<any>(apiUrl, usuario, {headers: httpHeaders});
  }

  guardarToken(accessToken: string) {
    this._token = accessToken;
    sessionStorage.setItem('token', accessToken);
  }

  guardarUsername(username: string) {
    this._username = username;
    sessionStorage.setItem('username', username);
  }

  guardarRole(role: string){
    this._role = role;
    sessionStorage.setItem('role', role);
  }

  isAuthenticated(): boolean{
    if(this.token != null && this.username != null && this.role != null){
      return true;
    }
    return false;
  }

  logout() {
    this._token = null;
    this._username = null;
    this._role = null;
    sessionStorage.clear();
  }
}
