package com.imalex28.crudclientes.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name="cliente")
public class Cliente {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long idCliente;
	
	private String nombre;
	private String apellidos;
	private String dni;
	private String email;

    public Cliente() {} 

    public Cliente(Long idCliente, String nombre, String apellidos, String dni, String email) {
        this.idCliente = idCliente;
        this.nombre = nombre;
        this.apellidos = apellidos;
        this.dni = dni;
        this.email = email;
    }
			
	public Long getIdCliente() { return idCliente; }
	public String getNombre() { return nombre; }
	public String getApellidos() { return apellidos; }
	public String getDni() { return dni; }
	public String getEmail() { return email; }
	
	public void setIdCliente(Long idCliente) { this.idCliente = idCliente; }
	public void setNombre(String nombre) { this.nombre = nombre; }
	public void setApellidos(String apellidos) { this.apellidos = apellidos; }
	public void setDni(String dni) { this.dni = dni; }
	public void setEmail(String email) { this.email = email; }
	
}

