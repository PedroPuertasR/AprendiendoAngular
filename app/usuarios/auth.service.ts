import { HttpClient, HttpHeaders} from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, map } from 'rxjs';
import { Usuario } from './usuario';

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  private _token: string;

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
    sessionStorage.setItem('token', this._token);
  }

  obtenerDatosToken(accessToken: string){
    if(accessToken != null){
      return JSON.parse(atob(accessToken.split(".")[1]));
    }
  }

}
