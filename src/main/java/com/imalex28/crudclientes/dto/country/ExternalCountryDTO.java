package com.imalex28.crudclientes.dto.country;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ExternalCountryDTO {
	//To-do: Remove public attributes, create getter and setters
	public Name name;
	@JsonProperty("capital")
	public List<String> capitalCity;
	public Map<String, String> languages; // { "spa": "Spanish", "glc": "Galician", ... }
	public static class Name { 
		public String common; 
		public String official; 
		}
}
