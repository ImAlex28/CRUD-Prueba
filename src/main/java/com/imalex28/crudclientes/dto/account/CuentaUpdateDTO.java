package com.imalex28.crudclientes.dto.account;

import jakarta.annotation.Nonnull;

public class CuentaUpdateDTO {
	@Nonnull
    private Long idCuenta;
	
    @Nonnull
    private Long idCliente;

    @Nonnull
    private String numeroCuenta;

    @Nonnull
    private String tipoCuenta;

    @Nonnull
    private double saldo;

    
	public CuentaUpdateDTO() {
	}


	public CuentaUpdateDTO(Long idCuenta, Long idCliente, String numeroCuenta, String tipoCuenta, double saldo) {
		this.idCuenta = idCuenta;
		this.idCliente = idCliente;
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


	public Long getIdCliente() {
		return idCliente;
	}


	public void setIdCliente(Long idCliente) {
		this.idCliente = idCliente;
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
