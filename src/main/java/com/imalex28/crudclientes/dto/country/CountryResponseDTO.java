package com.imalex28.crudclientes.dto.country;

import java.util.*;

public class CountryResponseDTO {
	public String name;
	public String capital;
	public List<String> languages;
	
	public CountryResponseDTO() {}

	public CountryResponseDTO(String name, String capital, List<String> languages) {
		this.name = name;
		this.capital = capital;
		this.languages = languages;
	}
}
