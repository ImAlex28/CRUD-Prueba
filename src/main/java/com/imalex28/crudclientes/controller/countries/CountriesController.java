package com.imalex28.crudclientes.controller.countries;

import java.util.List;

import com.imalex28.crudclientes.dto.country.CountryResponseDTO;
import com.imalex28.crudclientes.service.countries.CountryService;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@ApplicationScoped
@Path("/countries")
public class CountriesController {
	
	@Inject CountryService countryService;

    @GET 
    @Produces(MediaType.APPLICATION_JSON)
    public List<CountryResponseDTO> getCountries() {
      return countryService.listCountries();
    }
}
