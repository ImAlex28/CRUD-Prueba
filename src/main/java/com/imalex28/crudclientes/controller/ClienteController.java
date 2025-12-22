package com.imalex28.crudclientes.controller;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.inject.Inject;
import jakarta.json.Json;
import jakarta.json.JsonObject;

import java.util.List;
import java.util.Map;

import com.imalex28.crudclientes.dto.ClienteRequestDTO;
import com.imalex28.crudclientes.mapper.ClienteRequestMapper;
import com.imalex28.crudclientes.model.Cliente;
import com.imalex28.crudclientes.model.Cuenta;
import com.imalex28.crudclientes.service.ClienteService;
import com.imalex28.crudclientes.service.CuentaService;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
@Path("/clientes")
public class ClienteController {
	
	@Inject              
    ClienteService clienteService; 
	
	@Inject
	CuentaService cuentaService;
	
	@Inject
	ClienteRequestMapper clienteRequestMapper;
	
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
	@Produces(MediaType.APPLICATION_JSON)
	public Response create(ClienteRequestDTO clienteDTO) {
	    Cliente cliente = clienteRequestMapper.toCliente(clienteDTO);
	    clienteService.save(cliente);
		return Response.status(Response.Status.CREATED)
		 .entity(Map.of("id", cliente.getIdCliente()))
		 .build();	
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
	
	@GET // GET /clientes/id/cuentas
	@Path("/{id}/cuentas")
	@Produces(MediaType.APPLICATION_JSON)
	public List<Cuenta> findCuentasCliente(@PathParam("id") Long id) {
		return cuentaService.findByIdCliente(id);
	}
	
	@POST // POST /clientes/id/cuentas
	@Path("/{id}/cuentas")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response saveCuenta(@PathParam("id") Long idCliente, Cuenta cuenta) {
		
			Cliente cliente = new Cliente();
			
			cliente.setIdCliente(idCliente);
				
	        // Asociar solo el ID
	        cuenta.setIdCliente(cliente);

	        cuentaService.save(cuenta);

	        return Response.status(Response.Status.CREATED) //Devolvemos 201 y el objeto creado
	                       .entity(cuenta)
	                       .build();
	}
	
	@GET //GET /clientes/id/saldo
	@Path("/{id}/saldo")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getSaldoTotal(@PathParam("id") Long idCliente) {
		Double saldo = cuentaService.getSaldoTotalByCliente(idCliente);
		return Response.ok(Map.of("saldoCliente", saldo)).build();
	}	
}
