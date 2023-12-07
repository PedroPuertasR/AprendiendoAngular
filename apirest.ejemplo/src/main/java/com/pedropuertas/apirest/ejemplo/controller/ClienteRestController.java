package com.pedropuertas.apirest.ejemplo.controller;

import com.pedropuertas.apirest.ejemplo.model.Cliente;
import com.pedropuertas.apirest.ejemplo.service.ClienteService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.naming.Binding;
import javax.xml.crypto.Data;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@CrossOrigin(origins = {"http://localhost:4200"})
@RestController
@RequestMapping("/api")
public class ClienteRestController {

    @Autowired
    private ClienteService clienteService;

    @GetMapping("/clientes")
    public List<Cliente> index(){
        return clienteService.findAll();
    }

    @GetMapping("/clientes/page/{page}")
    public Page<Cliente> index(@PathVariable Integer page){
        return clienteService.findAll(PageRequest.of(page, 4));
    }

    @GetMapping("/clientes/{id}")
    public ResponseEntity<?> show(@PathVariable Long id){

        Cliente cliente = null;
        Map <String, Object> response = new HashMap<>();

        try{
            cliente = clienteService.findById(id);
        }catch(DataAccessException de){
            response.put("mensaje", "Error al realizar la consulta en la base de datos.");
            response.put("error", de.getMessage().concat(": ").concat(de.getMostSpecificCause().getMessage()));
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        };

        if(cliente == null){
            response.put("mensaje", "El cliente con ID ".concat(id.toString()).concat(" no existe en la base de datos."));
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<Cliente>(cliente, HttpStatus.OK);
    }

    @PostMapping("/clientes")
    public ResponseEntity<?> create(@Valid @RequestBody Cliente cliente, BindingResult result){
        Cliente nuevo = null;
        Map <String, Object> response = new HashMap<>();

        if(result.hasErrors()){
            List<String> errors = result.getFieldErrors()
                    .stream()
                    .map(err -> "El campo '" + err.getField() + "' " + err.getDefaultMessage())
                    .collect(Collectors.toList());

            response.put("errors", errors);
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.BAD_REQUEST);
        }

        try{
            nuevo = clienteService.save(cliente);
        }catch (DataAccessException e){
            response.put("mensaje", "Error al realizar el insert en la BBDD.");
            response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        response.put("mensaje", "El cliente ha sido creado con éxito.");
        response.put("cliente", nuevo);
        return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CREATED);
    }

    @PutMapping("/clientes/{id}")
    public ResponseEntity<?> update(@Valid @RequestBody Cliente cliente, BindingResult result, @PathVariable Long id){

        Cliente aux = clienteService.findById(id);
        Cliente update = null;

        Map <String, Object> response = new HashMap<>();

        if(result.hasErrors()){
            List<String> errors = result.getFieldErrors()
                    .stream()
                    .map(err -> "El campo '" + err.getField() + "' " + err.getDefaultMessage())
                    .collect(Collectors.toList());

            response.put("errors", errors);
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.BAD_REQUEST);
        }

        if(aux == null){
            response.put("mensaje", "Error: no se pudo editar, ya que el cliente con ID ".concat(id.toString()).concat(" no existe en la base de datos."));
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
        }

        try{
            aux.setApellidos(cliente.getApellidos());
            aux.setNombre(cliente.getNombre());
            aux.setEmail(cliente.getEmail());
            aux.setCreateAt(cliente.getCreateAt());

            update = clienteService.save(aux);
        }catch (DataAccessException e){
            response.put("mensaje", "Error al actualizar el cliente en la base de datos.");
            response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        response.put("mensaje", "El cliente ha sido actualizado con éxito.");
        response.put("cliente", update);

        return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CREATED);
    }

    @DeleteMapping("/clientes/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id){
        Map <String, Object> response = new HashMap<>();

        Cliente aux = clienteService.findById(id);

        if(aux == null){
            response.put("mensaje", "El cliente no existe en la base de datos, por lo que no se puede eliminar.");
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
        }

        try {

            Cliente cliente = clienteService.findById(id);

            String fotoAnterior = cliente.getFoto();

            if(fotoAnterior != null && fotoAnterior.length() > 0){
                Path rutaAnterior = Paths.get("uploads").resolve(fotoAnterior).toAbsolutePath();
                File archivoAnterior = rutaAnterior.toFile();

                if(archivoAnterior.exists() && archivoAnterior.canRead()){
                    archivoAnterior.delete();
                }

            }

            clienteService.delete(id);
        }catch (DataAccessException e){
            response.put("error", "No se ha podido borrar el cliente de la base de datos.");
            response.put("mensaje", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        response.put("mensaje", "Se ha borrado el cliente con éxito.");
        return new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);
    }

    @PostMapping("/clientes/upload")
    public ResponseEntity<?> upload(@RequestParam("archivo") MultipartFile archivo, @RequestParam("id") Long id){
        Map <String, Object> response = new HashMap<>();

        Cliente cliente = clienteService.findById(id);

        if(!archivo.isEmpty()){
            String nombreArchivo = UUID.randomUUID().toString().concat("_" + archivo.getOriginalFilename().replace( " ", ""));
            Path rutaArchivo = Paths.get("uploads/").resolve(nombreArchivo).toAbsolutePath();

            try {
                Files.copy(archivo.getInputStream(), rutaArchivo);
            } catch (IOException e) {
                response.put("error", "Error al subir la imagen: " + nombreArchivo);
                response.put("mensaje", e.getMessage().concat(": ").concat(e.getCause().getMessage()));
                return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
            }

            String fotoAnterior = cliente.getFoto();

            if(fotoAnterior != null && fotoAnterior.length() > 0){
                Path rutaAnterior = Paths.get("uploads").resolve(fotoAnterior).toAbsolutePath();
                File archivoAnterior = rutaAnterior.toFile();

                if(archivoAnterior.exists() && archivoAnterior.canRead()){
                    archivoAnterior.delete();
                }

            }

            cliente.setFoto(nombreArchivo);

            clienteService.save(cliente);

            response.put("cliente", cliente);
            response.put("mensaje", "Has subido correctamente la imagen " + nombreArchivo);
        }

        return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CREATED);
    }

}
