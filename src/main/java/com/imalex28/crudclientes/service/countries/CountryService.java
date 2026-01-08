package com.imalex28.crudclientes.service.countries;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.microprofile.rest.client.inject.RestClient;

import com.imalex28.crudclientes.apiclient.CountriesApiClient;
import com.imalex28.crudclientes.dto.country.CountryResponseDTO;
import com.imalex28.crudclientes.dto.country.ExternalCountryDTO;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class CountryService {

	@Inject 
	@RestClient 
	CountriesApiClient client;

	  
	public List<CountryResponseDTO> listCountries(){
		
		String parameters = "languages,capital,name";
		
		List<ExternalCountryDTO> externalResult = client.getAll(parameters);

		List<CountryResponseDTO> countries =
				externalResult.stream()
		        .<CountryResponseDTO>map(e -> {
		        	
		            return new CountryResponseDTO(
		                e.name == null ? "Unknown" : e.name.common,
		                (e.capitalCity == null || e.capitalCity.isEmpty()) ? "—" : e.capitalCity.get(0),
		                e.languages == null ? List.of() : new ArrayList<>(e.languages.values())
		            );
		        })
		        .collect(Collectors.toList());
		
		return countries;
	}
}
