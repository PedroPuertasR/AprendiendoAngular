import { Component } from '@angular/core';
import { AuthService } from '../usuarios/auth.service';
import { Router } from '@angular/router';
import Swal from 'sweetalert2';

@Component({
    selector: 'app-header',
    templateUrl: './header.component.html'
})
export class HeaderComponent {
    title: string = 'App Angular';

    constructor(public authService: AuthService, private router: Router){
    }

    logout(){
        this.authService.logout();
        this.router.navigate(['/login']);
        Swal.fire('Logout', 'Sesión cerrada con éxito', 'success');
    }
}