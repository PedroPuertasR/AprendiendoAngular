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

  ngOnInit(): void {
    if(this.authService.isAuthenticated()){
      //Swal.fire('Login', `Hola ${this.authService.username}, ya estás autenticado`, 'info');
      this.router.navigate(['/clientes']);
    }
  }

  login(): void{
    if(this.usuario.username == null || this.usuario.password == null){
      Swal.fire('Error login', 'El usuario o contraseña están vacíos', 'error');
      return;
    }

    this.authService.login(this.usuario).subscribe({
      next: (response) => {
        this.authService.guardarToken(response.accessToken);
        this.authService.guardarUsername(response.username);
        this.authService.guardarRole(response.role);

        let username = this.authService.username;

        this.router.navigate(['/clientes']);

        Swal.fire('Login correcto', `¡Bienvenido, ${username}!`, 'success');
      },
      error: (error) => {
        if(error.status == 400 || error.status == 401){
          Swal.fire('Error login', 'Usuario o contraseña incorrectos', 'error');
        }
      }
    })
  }
}
