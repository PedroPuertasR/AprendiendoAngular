package com.pedropuertas.apirest.ejemplo.security;

import com.pedropuertas.apirest.ejemplo.model.Role;
import com.pedropuertas.apirest.ejemplo.model.Usuario;
import com.pedropuertas.apirest.ejemplo.repository.UsuarioRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CustomUsersDetailsService implements UserDetailsService {

    private UsuarioRepository repository;

    @Autowired
    public CustomUsersDetailsService(UsuarioRepository repository) {
        this.repository = repository;
    }

    private Logger log = LoggerFactory.getLogger(CustomUsersDetailsService.class);

    public List<GrantedAuthority> mapToAutorities(List <Role> roles){
        return roles
                .stream()
                .map(role -> new SimpleGrantedAuthority(role.getNombre()))
                .peek(authority -> log.info("Role: " + authority.getAuthority()))
                .collect(Collectors.toList());
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Usuario usuarios = repository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

        return new User(usuarios.getUsername(), usuarios.getPassword(), mapToAutorities(usuarios.getRoles()));
    }
}
