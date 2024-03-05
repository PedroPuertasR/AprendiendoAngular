import { Component } from '@angular/core';
import { Usuario } from './usuario';
import Swal from 'sweetalert2';
import { AuthService } from './auth.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html'
})
export class LoginComponent {

  titulo:string = 'Por favor inicie sesión';
  usuario: Usuario;

  constructor(private authService: AuthService, private router: Router){
    this.usuario = new Usuario();
  }

  login(): void{
    if(this.usuario.username == null || this.usuario.password == null){
      Swal.fire('Error login', 'El usuario o contraseña están vacíos', 'error');
      return;
    }

    this.authService.login(this.usuario).subscribe(response => {
      this.authService.guardarToken(response.access_token);

      let token = this.authService.token;

      this.router.navigate(['/clientes']);
      Swal.fire('Login correcto', '¡Bienvenido!', 'success');
    })
  }
}
