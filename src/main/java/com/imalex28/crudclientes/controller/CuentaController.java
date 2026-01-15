package com.imalex28.crudclientes.controller;

import java.util.List;

import com.imalex28.crudclientes.dto.account.CuentaRequestDTO;
import com.imalex28.crudclientes.dto.account.CuentaResponseDTO;
import com.imalex28.crudclientes.dto.account.CuentaUpdateDTO;
import com.imalex28.crudclientes.mapper.ClienteRequestMapper;
import com.imalex28.crudclientes.mapper.ClienteResponseMapper;
import com.imalex28.crudclientes.mapper.CuentaUpdateMapper;
import com.imalex28.crudclientes.mapper.CuentaRequestMapper;
import com.imalex28.crudclientes.mapper.CuentaResponseMapper;
import com.imalex28.crudclientes.model.Cuenta;
import com.imalex28.crudclientes.service.ClienteService;
import com.imalex28.crudclientes.service.CuentaService;

import io.quarkus.security.Authenticated;
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
@Authenticated
public class CuentaController {
	
	@Inject
	CuentaService cuentaService;
	
	@Inject
	ClienteService clienteService;
	
	@Inject
	ClienteRequestMapper clienteRequestMapper;
	@Inject
	ClienteResponseMapper clienteResponseMapper;
	
	@Inject
	CuentaRequestMapper cuentaRequestMapper;
	@Inject
	CuentaResponseMapper cuentaResponseMapper;
	@Inject
	CuentaUpdateMapper cuentaPutMapper;
	
	@GET //GET /cuentas
	@Produces(MediaType.APPLICATION_JSON)  // Devuelve un JSON
	public List<CuentaResponseDTO> listAll() {
		List<Cuenta> cuentas = cuentaService.findAll();
	    return cuentaResponseMapper.toCuentaResponseDTOList(cuentas);
	}
	
	@GET //GET /cuentas/{id}
	@Path("/{id}") 
	@Produces(MediaType.APPLICATION_JSON)
	public CuentaResponseDTO findById(@PathParam("id") Long id) {
		Cuenta cuenta = cuentaService.findById(id);
		return cuentaResponseMapper.toCuentaResponseDTO(cuenta);
	}
	
	@POST //POST /cuentas
	@Consumes(MediaType.APPLICATION_JSON)
	public Response create(CuentaRequestDTO cuentaDTO) {
		Cuenta cuenta = cuentaRequestMapper.toCuenta(cuentaDTO, clienteService);
		cuentaService.save(cuenta);
	    return Response.status(201).build();  // 201 = Created
	}
	
	
	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	public Response update(CuentaUpdateDTO cuentaDTO) {
		Cuenta cuenta = cuentaPutMapper.toCuenta(cuentaDTO, clienteService);
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
