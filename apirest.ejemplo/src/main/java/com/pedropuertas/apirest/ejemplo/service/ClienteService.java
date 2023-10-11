package com.pedropuertas.apirest.ejemplo.service;

import com.pedropuertas.apirest.ejemplo.model.Cliente;
import com.pedropuertas.apirest.ejemplo.repository.ClienteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ClienteService {

    private final ClienteRepository repository;

    public List <Cliente> findAll(){
        return repository.findAll();
    }

}
