package com.pedropuertas.apirest.ejemplo.controller;

import com.pedropuertas.apirest.ejemplo.dtos.DtoAuthRespuesta;
import com.pedropuertas.apirest.ejemplo.dtos.Login;
import com.pedropuertas.apirest.ejemplo.dtos.Registro;
import com.pedropuertas.apirest.ejemplo.model.Cliente;
import com.pedropuertas.apirest.ejemplo.model.Region;
import com.pedropuertas.apirest.ejemplo.model.Role;
import com.pedropuertas.apirest.ejemplo.model.Usuario;
import com.pedropuertas.apirest.ejemplo.repository.RoleRepository;
import com.pedropuertas.apirest.ejemplo.repository.UsuarioRepository;
import com.pedropuertas.apirest.ejemplo.security.JwtTokenProvider;
import com.pedropuertas.apirest.ejemplo.service.ClienteService;
import com.pedropuertas.apirest.ejemplo.service.IUploadFileService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.*;
import java.util.stream.Collectors;

@CrossOrigin(origins = {"http://localhost:4200"})
@RestController
@RequestMapping("/api")
public class ClienteRestController {

    private AuthenticationManager authenticationManager;
    private PasswordEncoder passwordEncoder;
    private RoleRepository roleRepo;
    private UsuarioRepository usuarioRepository;
    private JwtTokenProvider tokenProvider;

    @Autowired
    public ClienteRestController(AuthenticationManager authenticationManager, PasswordEncoder passwordEncoder, RoleRepository roleRepo, UsuarioRepository usuarioRepository, JwtTokenProvider tokenProvider) {
        this.authenticationManager = authenticationManager;
        this.passwordEncoder = passwordEncoder;
        this.roleRepo = roleRepo;
        this.usuarioRepository = usuarioRepository;
        this.tokenProvider = tokenProvider;
    }

    @Autowired
    private ClienteService clienteService;

    @Autowired
    private IUploadFileService uploadService;

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
            aux.setRegion(cliente.getRegion());

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

            uploadService.eliminar(fotoAnterior);

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

            String nombreArchivo = null;

            try {
                nombreArchivo = uploadService.copiar(archivo);
            } catch (IOException e) {
                response.put("error", "Error al subir la imagen");
                response.put("mensaje", e.getMessage().concat(": ").concat(e.getCause().getMessage()));
                return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
            }

            String fotoAnterior = cliente.getFoto();

            uploadService.eliminar(fotoAnterior);

            cliente.setFoto(nombreArchivo);

            clienteService.save(cliente);

            response.put("cliente", cliente);
            response.put("mensaje", "Has subido correctamente la imagen " + nombreArchivo);
        }

        return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CREATED);
    }

    @GetMapping("/uploads/img/{nombreFoto:.+}")
    public ResponseEntity<Resource> verFoto(@PathVariable String nombreFoto){
        Resource recurso = null;

        try {
            recurso = uploadService.cargar(nombreFoto);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        HttpHeaders cabecera = new HttpHeaders();
        cabecera.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + recurso.getFilename() + "\"");

        return new ResponseEntity<Resource>(recurso, cabecera, HttpStatus.OK);
    }

    @GetMapping("/clientes/regiones")
    public List <Region> listarRegiones(){
        return clienteService.findAllRegiones();
    }


    //Método para poder registrar usuarios con Rol USER
    @PostMapping("/auth/registro")
    public ResponseEntity<String> registrar(@RequestBody Registro dtoRegistro){
        if(usuarioRepository.existsByUsername(dtoRegistro.getUsername())){
            return new ResponseEntity<>("El usuario ya existe. Inténtalo con otro.", HttpStatus.BAD_REQUEST);
        }

        Usuario usuario = new Usuario();
        usuario.setUsername(dtoRegistro.getUsername());
        usuario.setPassword(passwordEncoder.encode(dtoRegistro.getPassword()));

        Role roles = roleRepo.findByNombre("USER").get();

        usuario.setRoles(Collections.singletonList(roles));

        usuarioRepository.save(usuario);

        return new ResponseEntity<String>("Registro existoso.", HttpStatus.OK);
    }

    //Método para poder registrar usuarios con Rol ADMIN
    @PostMapping("/auth/registroAdmin")
    public ResponseEntity<String> registrarAdmin(@RequestBody Registro dtoRegistro){
        if(usuarioRepository.existsByUsername(dtoRegistro.getUsername())){
            return new ResponseEntity<>("El admin ya existe. Inténtalo con otro.", HttpStatus.BAD_REQUEST);
        }

        Usuario usuario = new Usuario();
        usuario.setUsername(dtoRegistro.getUsername());
        usuario.setPassword(passwordEncoder.encode(dtoRegistro.getPassword()));

        Role roles = roleRepo.findByNombre("ADMIN").get();

        usuario.setRoles(Collections.singletonList(roles));

        usuarioRepository.save(usuario);

        return new ResponseEntity<String>("Registro existoso.", HttpStatus.OK);
    }

    //Método para loguear como un USER y obtener un token
    @PostMapping("/auth/login")
    public ResponseEntity<DtoAuthRespuesta> login(@RequestBody Login dtoLogin){
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                dtoLogin.getUsername(),
                dtoLogin.getPassword()
        ));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String token = tokenProvider.generarToken(authentication);
        String username = dtoLogin.getUsername();
        List <String> roleList = usuarioRepository.findUsuario(username).get().getRoles()
                .stream()
                .map(Role::getNombre)
                .toList();

        String role = roleList.get(0);

        return new ResponseEntity<DtoAuthRespuesta>(new DtoAuthRespuesta(token, username, role), HttpStatus.OK);
    }
}
