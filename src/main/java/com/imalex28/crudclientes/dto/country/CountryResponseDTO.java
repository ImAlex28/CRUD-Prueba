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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCapital() {
		return capital;
	}

	public void setCapital(String capital) {
		this.capital = capital;
	}

	public List<String> getLanguages() {
		return languages;
	}

	public void setLanguages(List<String> languages) {
		this.languages = languages;
	}
}
