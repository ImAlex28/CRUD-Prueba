package com.imalex28.crudclientes.dto.client;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.annotation.Nonnull;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ClienteRequestDTO {
	@Nonnull
	public String nombre;
	
	@Nonnull
	public String apellidos;
	
	@Nonnull
	public String dni;
	
	@Nonnull
	public String email;

	public ClienteRequestDTO() {
	}

	public ClienteRequestDTO(String nombre, String apellidos, String dni, String email) {
		this.nombre = nombre;
		this.apellidos = apellidos;
		this.dni = dni;
		this.email = email;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getApellidos() {
		return apellidos;
	}

	public void setApellidos(String apellidos) {
		this.apellidos = apellidos;
	}

	public String getDni() {
		return dni;
	}

	public void setDni(String dni) {
		this.dni = dni;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
	
}
