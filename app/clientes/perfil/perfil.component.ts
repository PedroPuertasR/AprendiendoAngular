import { Component } from '@angular/core';
import { Cliente } from '../cliente';
import { ClienteService } from '../cliente.service';
import { ActivatedRoute } from '@angular/router';
import Swal from 'sweetalert2';

@Component({
  selector: 'perfil-cliente',
  templateUrl: './perfil.component.html',
  styleUrls: ['./perfil.component.css']
})
export class PerfilComponent {

  cliente: Cliente;
  titulo: string = "Perfil del cliente"
  fotoSeleccionada: File;

  constructor(private clienteService: ClienteService, 
    private activatedRoute: ActivatedRoute){ }

  ngOnInit() {
    this.activatedRoute.paramMap.subscribe(params => {
      let id: number = +params.get('id');

      if(id){
        this.clienteService.getCliente(id).subscribe(cliente => {
          this.cliente = cliente;
        });
      }
    });
  }

  seleccionarFoto(event){
    this.fotoSeleccionada = event.target.files[0];
    console.log(this.fotoSeleccionada);

    if(this.fotoSeleccionada.type.indexOf('image') < 0){
      Swal.fire('Error:', 'El archivo seleccionado no es una imagen', 'error');
      this.fotoSeleccionada = null;
    }
  }

  subirFoto(){

    if(!this.fotoSeleccionada){
      Swal.fire('Error:', 'No ha seleccionado ninguna imagen', 'error');
    }else{
      this.clienteService.subirFoto(this.fotoSeleccionada, this.cliente.id).subscribe(cliente =>{
        this.cliente = cliente;
        Swal.fire('La foto se ha subido correctamente', `La foto se ha subido con éxito: ${this.cliente.foto}`, 'success');
      });
    }

  }

}
