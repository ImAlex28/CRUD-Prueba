package com.imalex28.crudclientes.controller.ip;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import com.imalex28.crudclientes.dto.ip.IPResponseDTO;
import com.imalex28.crudclientes.service.ip.IpCacheService;

import io.quarkus.security.Authenticated;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@ApplicationScoped
@Path("/ipdata")
@Authenticated
public class IPController {
	
	@Inject IpCacheService ipService;

    @ConfigProperty(name = "ip.api.token")
	private
    String token;

	
	@GET
	@Path("/{ip}")
    @Produces(MediaType.APPLICATION_JSON)
	public IPResponseDTO getCountries(@PathParam("ip") String ip) {
	      return ipService.getIP( ip , getToken());
	}
	
    @DELETE
    @Path("/cache")
    //@RolesAllowed("admin") Esto se podría usar para un rol concreto, de momento no lo usaré porque no hay roles.
    public Response invalidateAllCache() {
    	ipService.invalidateAll();
        return Response.noContent().build(); // HTTP 204
    }

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}
}
