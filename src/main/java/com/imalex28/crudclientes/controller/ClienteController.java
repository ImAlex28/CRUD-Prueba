package com.imalex28.crudclientes.controller;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.inject.Inject;

import java.util.List;

import com.imalex28.crudclientes.model.Cliente;
import com.imalex28.crudclientes.service.ClienteService;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
@Path("/clientes")
public class ClienteController {
	
	@Inject              
    ClienteService clienteService; 
	
	@GET  // GET /clientes
	@Produces(MediaType.APPLICATION_JSON)  // Devuelve un JSON
	public List<Cliente> listAll() {
	    return clienteService.findAll();
	}
	
	@GET
	@Path("/{id}") 
	@Produces(MediaType.APPLICATION_JSON)
	public Cliente findById(@PathParam("id") Long id) {
		return clienteService.findById(id);
	}
	
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	public Response create(Cliente cliente) {
	    clienteService.save(cliente);
	    return Response.status(201).build();  // 201 = Created
	}
	
	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	public Response update(Cliente cliente) {
	    clienteService.update(cliente);
	    return Response.ok().build();  // 200 OK
	}
	
	@DELETE
	@Path("/{id}") 
	public Response delete(@PathParam("id") Long id) {
		clienteService.delete(id);
		return Response.noContent().build(); 
	}
	
}
