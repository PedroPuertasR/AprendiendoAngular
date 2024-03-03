import { Component } from '@angular/core';
import { Usuario } from './usuario';
import Swal from 'sweetalert2';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html'
})
export class LoginComponent {

  titulo:string = 'Por favor inicie sesión';
  usuario: Usuario;

  constructor(){
    this.usuario = new Usuario();
  }

  login():void{
    console.log(this.usuario);
    if(this.usuario.username == null || this.usuario.password == null){
      Swal.fire('Error al iniciar sesión', 'Nombre de usuario o contraseña vacíos', 'error');
      return;
    }
  }

}
