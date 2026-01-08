package com.imalex28.crudclientes.dto.client;

import jakarta.annotation.Nonnull;

public class ClienteResponseDTO {
	@Nonnull
	public Long id;
	
	@Nonnull
	public String nombre;
	
	@Nonnull
	public String apellidos;
	
	@Nonnull
	public String dni;
	
	@Nonnull
	public String email;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
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

	public ClienteResponseDTO() {
	}

	public ClienteResponseDTO(Long id, String nombre, String apellidos, String dni, String email) {
		this.id = id;
		this.nombre = nombre;
		this.apellidos = apellidos;
		this.dni = dni;
		this.email = email;
	}
}
