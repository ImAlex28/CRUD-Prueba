package com.imalex28.crudclientes.model;

public class Cuenta {

    private Long idCuenta;
    private Long idCliente;
    private String numeroCuenta;
    private String tipoCuenta;
    private double saldo;

    public Cuenta() {}

    public Cuenta(Long idCuenta, Long idCliente, String numeroCuenta, String tipoCuenta, double saldo) {
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

