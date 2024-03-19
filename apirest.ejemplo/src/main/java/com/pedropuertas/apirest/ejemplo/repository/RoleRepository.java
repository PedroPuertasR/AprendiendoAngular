package com.pedropuertas.apirest.ejemplo.repository;

import com.pedropuertas.apirest.ejemplo.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

    public Optional<Role> findByNombre(String nombre);

    public Optional<Role> findById(int id);

}
