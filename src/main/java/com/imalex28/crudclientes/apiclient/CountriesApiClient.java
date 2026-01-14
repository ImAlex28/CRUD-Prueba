package com.imalex28.crudclientes.apiclient;

import java.util.List;

import com.imalex28.crudclientes.dto.country.ExternalCountryDTO;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@RegisterRestClient(configKey="countries-api")
@Path("/v3.1")
public interface CountriesApiClient {
	@GET
	@Path("/all")
	@Produces(MediaType.APPLICATION_JSON)
	List<ExternalCountryDTO> getAll(@QueryParam("fields") String fields);
}
