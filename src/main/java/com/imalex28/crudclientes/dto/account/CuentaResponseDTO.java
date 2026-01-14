package com.imalex28.crudclientes.dto.account;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.imalex28.crudclientes.model.Cliente;

import jakarta.annotation.Nonnull;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class CuentaResponseDTO {
	@Nonnull
    private Long idCuenta;
    
	@Nonnull
    private Cliente cliente;
	
	@Nonnull
    private String numeroCuenta;
	
	@Nonnull
    private String tipoCuenta;
	
	@Nonnull
    private double saldo;

	public CuentaResponseDTO() {
	}

	public CuentaResponseDTO(Long idCuenta, Cliente cliente, String numeroCuenta, String tipoCuenta, double saldo) {
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

	public Cliente getCliente() {
		return cliente;
	}

	public void setCliente(Cliente cliente) {
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
