package com.imalex28.crudclientes.service;

import java.util.List;

import com.imalex28.crudclientes.model.Cuenta;
import com.imalex28.crudclientes.repository.ClienteRepository;
import com.imalex28.crudclientes.repository.CuentaRepository;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

@ApplicationScoped
public class CuentaService {
	
    @Inject
    @Named("jpa-cuenta")
    CuentaRepository cuentaRepository;
    

    @Inject
    @Named("jpa")
    ClienteRepository clienteRepository;
    
    // Métodos privados de validación

    private void validarClienteExiste(Long idCliente) {
        if (!clienteRepository.existsById(idCliente)) {
            throw new IllegalArgumentException("El cliente no existe");
        }
    }

    private void validarCuentaExiste(Long idCuenta) {
        if (!cuentaRepository.existsById(idCuenta)) {
            throw new IllegalArgumentException("La cuenta no existe");
        }
    }

	public List<Cuenta> findAll(){
		return cuentaRepository.findAll();
	}
	
	public Cuenta findById(Long id) {
		return cuentaRepository.findById(id);
	}
	

	public void save(Cuenta cuenta) {
        validarClienteExiste(cuenta.getIdCliente().getIdCliente());
		cuentaRepository.save(cuenta);
	}
	

    public void update(Cuenta cuenta) {
        validarClienteExiste(cuenta.getIdCliente().getIdCliente());
        validarCuentaExiste(cuenta.getIdCuenta());
    	cuentaRepository.update(cuenta);
    }
    

    public void delete(Long id) {
        validarCuentaExiste(id);
    	cuentaRepository.delete(id);
    }

	public List<Cuenta> findByIdCliente(Long idCliente) {
		return cuentaRepository.findByIdCliente(idCliente);
	}

	public Double getSaldoTotalByCliente(Long idCliente) {
		return cuentaRepository.getSaldoTotalByCliente(idCliente);
	}
}
