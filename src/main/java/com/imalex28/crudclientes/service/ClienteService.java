package com.imalex28.crudclientes.service;

import java.util.List;

import com.imalex28.crudclientes.model.Cliente;
import com.imalex28.crudclientes.repository.ClienteRepository;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.ws.rs.NotFoundException;

@ApplicationScoped
public class ClienteService {

    @Inject
    @Named("jpa")
    ClienteRepository clienteRepository;

    public List<Cliente> findAll() {
    	// Llamamos al servicio de la capa Repositorio
        return clienteRepository.findAll();
    }

    public Cliente findById(Long id) {
        Cliente cliente = clienteRepository.findById(id);
        if (cliente == null) {
        	// Si el cliente no se encuentra
            throw new NotFoundException("Cliente con ID " + id + " no encontrado");
        }
        return cliente;
    }

    public void save(Cliente cliente) {
    	// Llamamos directamente al servicio de la capa Repo
        clienteRepository.save(cliente);
    }

    public void update(Cliente cliente) {
        Cliente existente = clienteRepository.findById(cliente.getIdCliente());
        if (existente == null) {
        	// Si el cliente no existe:
            throw new NotFoundException("Cliente con ID " + cliente.getIdCliente() + " no encontrado");
        }
        
        clienteRepository.update(cliente);
    }

    public void delete(Long id) {
        Cliente existente = clienteRepository.findById(id);
        if (existente == null) {
        	// Si el cliente no existe:
            throw new NotFoundException("Cliente con ID " + id + " no encontrado");
        }
        // Si existe, ya llamamos al delete
        clienteRepository.delete(id);
    }
}
