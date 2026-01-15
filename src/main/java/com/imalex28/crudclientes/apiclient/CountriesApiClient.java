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
	
	
	
	/**
	 * GET to the EXTERNAL API for countries. URL should be configured in Application.Properties
	 * 
	 * This API returns a JSON with a list of all the countries, filtering only the information requested in the "fields"
	 * 
	 * @param fields
	 * @return List<ExternalCountryDTO>
	 */
	@GET
	@Path("/all")
	@Produces(MediaType.APPLICATION_JSON)
	List<ExternalCountryDTO> getAll(@QueryParam("fields") String fields);
}
