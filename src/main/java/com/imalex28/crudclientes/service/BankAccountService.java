package com.imalex28.crudclientes.service;

import java.util.List;

import com.imalex28.crudclientes.model.BankAccount;
import com.imalex28.crudclientes.repository.BankAccountRepository;
import com.imalex28.crudclientes.repository.ClientRepository;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

@ApplicationScoped
public class BankAccountService {
	
    @Inject
    @Named("jpa-cuenta")
    BankAccountRepository cuentaRepository;
    

    @Inject
    @Named("jpa")
    ClientRepository clienteRepository;
    
    // Métodos privados de validación

    private void validarClienteExiste(Long clientId) {
        if (!clienteRepository.existsById(clientId)) {
            throw new IllegalArgumentException("El cliente no existe");
        }
    }

    private void validarCuentaExiste(Long accountId) {
        if (!cuentaRepository.existsById(accountId)) {
            throw new IllegalArgumentException("La cuenta no existe");
        }
    }

	public List<BankAccount> findAll(){
		return cuentaRepository.findAll();
	}
	
	public BankAccount findById(Long id) {
		return cuentaRepository.findById(id);
	}
	

	public void save(BankAccount account) {
        validarClienteExiste(account.getClient().getClientId());
		cuentaRepository.save(account);
	}
	

    public void update(BankAccount account) {
        validarClienteExiste(account.getClient().getClientId());
        validarCuentaExiste(account.getBankAccountId());
    	cuentaRepository.update(account);
    }
    

    public void delete(Long id) {
        validarCuentaExiste(id);
    	cuentaRepository.delete(id);
    }

	public List<BankAccount> findByIdCliente(Long clientId) {
		return cuentaRepository.findByIdCliente(clientId);
	}

	public Double getSaldoTotalByCliente(Long clientId) {
		return cuentaRepository.getSaldoTotalByCliente(clientId);
	}
}
