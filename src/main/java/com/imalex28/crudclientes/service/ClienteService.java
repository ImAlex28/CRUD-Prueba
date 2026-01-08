package com.imalex28.crudclientes.service;

import java.util.List;

import com.imalex28.crudclientes.dto.ErrorResponseDTO;
import com.imalex28.crudclientes.model.Cliente;
import com.imalex28.crudclientes.repository.ClienteRepository;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

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

        String emailNorm = normalizeEmail(cliente.getEmail());

        cliente.setEmail(emailNorm);



        // Check de unicidad
             Cliente existente = clienteRepository.findByEmail(emailNorm);
				if (existente != null) {
				        throw new WebApplicationException(
				            Response.status(Response.Status.CONFLICT)
				                    .type(MediaType.APPLICATION_JSON)
				                    .entity(new ErrorResponseDTO(409, "Ya existe un cliente con el email " + emailNorm))
				                    .build()
				        );

				} 
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
    
    // Métodos helper internos
    private String normalizeEmail(String email) {
        return email == null ? null : email.trim().toLowerCase();
    }

}
