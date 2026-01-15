package com.imalex28.crudclientes.controller.countries;

import java.util.List;

import com.imalex28.crudclientes.dto.country.CountryResponseDTO;
import com.imalex28.crudclientes.service.countries.CountryCacheService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@ApplicationScoped
@Path("/countries")
public class CountriesController {
	
	@Inject CountryCacheService countryService;

    @GET 
    @Produces(MediaType.APPLICATION_JSON)
    public List<CountryResponseDTO> getCountries() {
      return countryService.listCountries();
    }

    @DELETE
    @Path("/cache")
    public Response invalidateAllCache() {
        countryService.invalidateAll();
        return Response.noContent().build(); // HTTP 204
    }

}
