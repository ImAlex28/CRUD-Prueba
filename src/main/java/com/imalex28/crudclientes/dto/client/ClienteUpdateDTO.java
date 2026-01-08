package com.imalex28.crudclientes.dto.client;

import jakarta.annotation.Nonnull;

public class ClienteUpdateDTO {
	
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

		public ClienteUpdateDTO() {
		}

		public ClienteUpdateDTO(Long idCliente, String nombre, String apellidos, String dni, String email) {
			this.id = idCliente;
			this.nombre = nombre;
			this.apellidos = apellidos;
			this.dni = dni;
			this.email = email;
		}

		public String getNombre() {
			return nombre;
		}
		

		public Long getId() {
			return id;
		}

		public void setId(Long idCliente) {
			this.id = idCliente;
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
