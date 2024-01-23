import { Component, Input } from '@angular/core';
import { Cliente } from '../cliente';
import { ClienteService } from '../cliente.service';
import Swal from 'sweetalert2';
import { HttpEventType } from '@angular/common/http';
import { ModalService } from './modal.service';

@Component({
  selector: 'perfil-cliente',
  templateUrl: './perfil.component.html',
  styleUrls: ['./perfil.component.css']
})
export class PerfilComponent {

  @Input() cliente: Cliente;
  titulo: string = "Perfil del cliente"
  fotoSeleccionada: File;
  progreso: number = 0;

  constructor(private clienteService: ClienteService,
    public modalService: ModalService){ }

  ngOnInit() {
    /* this.activatedRoute.paramMap.subscribe(params => {
      let id: number = +params.get('id');

      if(id){
        this.clienteService.getCliente(id).subscribe(cliente => {
          this.cliente = cliente;
        });
      }
    }); */
  }

  seleccionarFoto(event){
    this.fotoSeleccionada = event.target.files[0];
    this.progreso = 0;
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
      this.clienteService.subirFoto(this.fotoSeleccionada, this.cliente.id).subscribe(event =>{
        if(event.type === HttpEventType.UploadProgress){
          this.progreso = Math.round((event.loaded / event.total) * 100);
        }else if(event.type === HttpEventType.Response){
          let response: any = event.body;
          this.cliente = response.cliente as Cliente;

          this.modalService.notificarUpload.emit(this.cliente);
          Swal.fire('La foto se ha subido correctamente', response.mensaje, 'success');
        }
      });
    }

  }

  cerrarModal(){
    this.modalService.cerrarModal();
    this.fotoSeleccionada = null;
    this.progreso = 0;
  }

}
