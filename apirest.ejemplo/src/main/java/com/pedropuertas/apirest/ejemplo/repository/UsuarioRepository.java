package com.pedropuertas.apirest.ejemplo.repository;

import com.pedropuertas.apirest.ejemplo.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    public Usuario findByUsername(String username);

    @Query("select u from Usuario u where u.username =?1")
    public Usuario findUsuario(String username);

}
