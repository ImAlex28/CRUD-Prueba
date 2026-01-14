package com.imalex28.crudclientes.apiclient;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import com.imalex28.crudclientes.dto.ip.ExternalIPDTO;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;

@RegisterRestClient(configKey="ip-api")
@Path("/lite")
public interface IPApiClient {
	
	/**
	 * GET to the external API for IP. Response is a JSON with IP data.
	 * @param ip
	 * @param token
	 * @return ExternalIPDTO
	 */
	@GET
	@Path("/{ip}")
	@Produces(MediaType.APPLICATION_JSON)
	ExternalIPDTO getIP(@PathParam("ip") String ip, @QueryParam("token") String token);
}
