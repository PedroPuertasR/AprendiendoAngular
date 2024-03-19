import { Injectable } from '@angular/core';
//import { CLIENTES } from './clientes.json';
import { Cliente } from './cliente';
import { of, Observable, throwError, tap} from 'rxjs';
import { HttpClient, HttpEvent, HttpHeaders, HttpRequest } from '@angular/common/http';
import { map , catchError} from 'rxjs/operators';
import Swal from 'sweetalert2';
import { Router } from '@angular/router';
import { Region } from './region';
import { AuthService } from '../usuarios/auth.service';
//import { formatDate } from '@angular/common';


@Injectable({
  providedIn: 'root'
})
export class ClienteService {

  private urlEndPoint:string = 'http://localhost:9000/api/clientes';
  private httpHeaders = new HttpHeaders({'Content-Type': 'application/json'});

  constructor(private http: HttpClient, private router: Router, private authService: AuthService) { }

  private isNotAuthorized(e) : boolean{
    if(e.status == 401 && this.authService.role == null){
      Swal.fire('Acceso denegado', 'No está logueado', 'warning');
      this.router.navigate(['/login']);
      return true;
    }
    
    if(e.status == 401 && this.authService.role == 'USER'){
      Swal.fire('Acceso denegado', `Hola ${this.authService.username} no tienes acceso a este recurso`, 'warning');
      this.router.navigate(['/clientes']);
      return true;
    }

    return false;
  }

  private addAuthorizationHeader(){
    let token = this.authService.token;

    if(token != null){
      return this.httpHeaders.append('Authorization', 'Bearer ' + token);
    }

    return this.httpHeaders;
  }

  getClientes(page: number) : Observable<any>{
    //return of(CLIENTES);
    return this.http.get(this.urlEndPoint + '/page/' + page).pipe(
      map((response: any) => {
        (response.content as Cliente[]).map(cliente => {
          cliente.nombre = cliente.nombre.toUpperCase();
          //También se puede utilizar la clase DatePipe de @angular/common de la siguiente forma:
          //let datePipe = new DatePipe('en-US');
          //cliente.createAt = datePipe.transform(cliente.createAt, 'dd/MM/yyyy');
          //Para poder utilizar 'es' hace falta importar el sistema local en la clase, o en este caso en el app.module
          //cliente.createAt = formatDate(cliente.createAt, 'EEEE dd/MMM/yyyy', 'es'); 
          //Aunque en este ejemplo lo realizaremos directamente en el HTML de clientes, importando en app.module el LOCALE_ID y añadiendolo a providers
          return cliente;
        });
        return response;
      }),
      tap((response: any) => {
        let clientes = response.content as Cliente[];
        clientes.forEach(cliente => {
          console.log(cliente.nombre);
        })
      })
    );
  }

  //En este método se ha manejado el map del backend como un tipo cliente, gracias al operador map que hay dentro del return
  //MIRAR TAMBIÉN EL MÉTODO CREATE DENTRO DEL FORM.COMPONENT
  create(cliente : Cliente) : Observable<Cliente> {
    return this.http.post(this.urlEndPoint, cliente, {headers: this.addAuthorizationHeader()}).pipe(
      map((response: any) => response.cliente as Cliente),
      catchError(e => {

        if(this.isNotAuthorized(e)){
          return throwError(() => e);
        }

        if(e.status==400){
          return throwError(() => e);
        }

        console.error(e.error.mensaje);
        Swal.fire(e.error.mensaje, e.error.error, 'error');
        return throwError(() => e);
      })
    );
  }

  getCliente(id) : Observable<Cliente> {
    return this.http.get<Cliente>(`${this.urlEndPoint}/${id}`, {headers: this.addAuthorizationHeader()}).pipe(
      catchError(e => {
        if(this.isNotAuthorized(e)){
          return throwError(() => e);
        }

        this.router.navigate(['/clientes']);
        console.error(e.error.mensaje);
        Swal.fire('Error al editar', e.error.mensaje, 'error');
        return throwError(() => e);
      })
    );
  }

  //En este método se ha manejado la respuesta del backend como un tipo any, el cual se desglosará en el método update del form.component
  update(cliente: Cliente) : Observable<any> {
    return this.http.put<any>(`${this.urlEndPoint}/${cliente.id}`, cliente, {headers: this.addAuthorizationHeader()}).pipe(
      catchError(e => {
        if(this.isNotAuthorized(e)){
          return throwError(() => e);
        }

        if(e.status==400){
          return throwError(() => e);
        }

        console.error(e.error.mensaje);
        Swal.fire(e.error.mensaje, e.error.error, 'error');
        return throwError(() => e);
      })
    )
  }

  delete(id: number) : Observable<Cliente> {
    return this.http.delete<Cliente>(`${this.urlEndPoint}/${id}`, {headers: this.addAuthorizationHeader()}).pipe(
      catchError(e => {
        if(this.isNotAuthorized(e)){
          return throwError(() => e);
        }

        console.error(e.error.mensaje);
        Swal.fire(e.error.mensaje, e.error.error, 'error');
        return throwError(() => e);
      })
    )
  }

  subirFoto(archivo: File, id): Observable<HttpEvent<{}>>{
    let formData = new FormData();

    formData.append("archivo", archivo);
    formData.append("id", id);

    let httpHeaders = new HttpHeaders();
    let token = this.authService.token;

    if(token != null){
       httpHeaders = httpHeaders.append('Authorization', 'Bearer ' + token);
    }

    const req = new HttpRequest('POST', `${this.urlEndPoint}/upload`, formData, {
      reportProgress: true,
      headers: httpHeaders
    });

    return this.http.request(req).pipe(
      catchError(e => {
        this.isNotAuthorized(e);
        return throwError(() => e);
      })
    );
  }

  getRegiones(): Observable <Region[]> {
    return this.http.get<Region[]>(this.urlEndPoint + '/regiones', {headers: this.addAuthorizationHeader()}).pipe(
      catchError(e => {
        this.isNotAuthorized(e);
        return throwError(() => e);
      })
    );
  }
}
