import { Component } from '@angular/core';
import { Cliente } from './cliente';
import { ClienteService } from './cliente.service';
import { Router, ActivatedRoute } from '@angular/router';
import Swal from 'sweetalert2';

@Component({
  selector: 'app-form',
  templateUrl: './form.component.html'
})
export class FormComponent {

  public cliente: Cliente = new Cliente();
  public titulo: string = "Crear cliente";

  public errores: string[];

  constructor(private clienteService: ClienteService, private router: Router, private activate: ActivatedRoute) { }

  ngOnInit(){
    this.cargarCliente()    
  }

  cargarCliente(): void {
    this.activate.params.subscribe(params => {
      let id = params['id']

      if (id) {
        this.clienteService.getCliente(id).subscribe(
          (cliente) => this.cliente = cliente
        )
      }
    }

    )
  }

  create(): void {
    this.clienteService.create(this.cliente).subscribe(
      cliente => {
        Swal.fire('Nuevo cliente', `El cliente <b>${cliente.nombre}</b> ha sido creado con éxito.`, 'success'),
          this.router.navigate(['/clientes'])
      },
      err => {
        this.errores = err.error.errors as string[];
        console.error('Código del error desde el back: ' + err.status);
        console.error(err.error.errors);
      }
    )
  }

  //En este método utilizamos json y accedemos al cliente para indicar la respuesta en el popup
  update(): void{
    this.clienteService.update(this.cliente).subscribe(
      json => {
        this.router.navigate(['/clientes']),
        Swal.fire('Cliente actualizado', `${json.mensaje} ${json.cliente.nombre}`, 'success')
      },
      err => {
        this.errores = err.error.errors as string[];
        console.error('Código del error desde el back: ' + err.status);
        console.error(err.error.errors);
      }
    )
  }

}
