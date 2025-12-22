package com.imalex28.crudclientes.controller;

import java.util.List;

import com.imalex28.crudclientes.model.Cliente;
import com.imalex28.crudclientes.model.Cuenta;
import com.imalex28.crudclientes.service.CuentaService;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@ApplicationScoped
@Path("/cuentas")
public class CuentaController {
	
	@Inject
	CuentaService cuentaService;
	
	@GET //GET /cuentas
	@Produces(MediaType.APPLICATION_JSON)  // Devuelve un JSON
	public List<Cuenta> listAll() {
	    return cuentaService.findAll();
	}
	
	@GET //GET /cuentas/{id}
	@Path("/{id}") 
	@Produces(MediaType.APPLICATION_JSON)
	public Cuenta findById(@PathParam("id") Long id) {
		return cuentaService.findById(id);
	}
	
	@POST //POST /cuentas
	@Consumes(MediaType.APPLICATION_JSON)
	public Response create(Cuenta cuenta) {
		cuentaService.save(cuenta);
	    return Response.status(201).build();  // 201 = Created
	}
	
	
	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	public Response update(Cuenta cuenta) {
		cuentaService.update(cuenta);
	    return Response.ok().build();  // 200 OK
	}
	
	@DELETE
	@Path("/{id}") 
	public Response delete(@PathParam("id") Long id) {
		cuentaService.delete(id);
		return Response.noContent().build(); 
	}


	
	
}
