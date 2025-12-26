package com.imalex28.crudclientes.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name="cuenta")
public class Cuenta {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id_cuenta")
    private Long idCuenta;
    
	@ManyToOne(fetch = FetchType.LAZY) // Esto es para que el cliente no se cargue con cada Query, solo se carga si se accede a sus atributos en la Query.
	@JoinColumn(name = "id_cliente", nullable = false)
    private Cliente cliente;
	
	@Column(name = "numero_cuenta" ,unique = true, nullable = false)
    private String numeroCuenta;
	
	@Column(name = "tipo_cuenta" ,nullable = false)
    private String tipoCuenta;
	
	@Column(nullable = false)
    private double saldo;

    public Cuenta() {}

    public Cuenta(Long idCuenta, Cliente cliente, String numeroCuenta, String tipoCuenta, double saldo) {
        this.idCuenta = idCuenta;
        this.cliente = cliente;
        this.numeroCuenta = numeroCuenta;
        this.tipoCuenta = tipoCuenta;
        this.saldo = saldo;
    }

	public Long getIdCuenta() {
		return idCuenta;
	}

	public void setIdCuenta(Long idCuenta) {
		this.idCuenta = idCuenta;
	}

	public Cliente getIdCliente() {
		return cliente;
	}

	public void setIdCliente(Cliente cliente) {
		this.cliente = cliente;
	}

	public String getNumeroCuenta() {
		return numeroCuenta;
	}

	public void setNumeroCuenta(String numeroCuenta) {
		this.numeroCuenta = numeroCuenta;
	}

	public String getTipoCuenta() {
		return tipoCuenta;
	}

	public void setTipoCuenta(String tipoCuenta) {
		this.tipoCuenta = tipoCuenta;
	}

	public double getSaldo() {
		return saldo;
	}

	public void setSaldo(double saldo) {
		this.saldo = saldo;
	}
   
}

