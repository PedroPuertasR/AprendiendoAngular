package com.pedropuertas.apirest.ejemplo.repository;

import com.pedropuertas.apirest.ejemplo.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    public Optional<Usuario> findByUsername(String username);

    @Query("select u from Usuario u where u.username =?1")
    public Optional<Usuario> findUsuario(String username);

    public Boolean existsByUsername(String username);

}
