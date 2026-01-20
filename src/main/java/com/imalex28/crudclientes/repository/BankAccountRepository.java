package com.imalex28.crudclientes.repository;

import java.util.List;

import com.imalex28.crudclientes.model.BankAccount;

public interface BankAccountRepository {
    List<BankAccount> findAll();
    BankAccount findById(Long id);
 
    // Recibe Long idCliente (el ID del cliente)
    // Internamente JPA hará la búsqueda por cliente.idCliente
    List<BankAccount> findByIdCliente(Long idCliente);
    
    Double getSaldoTotalByCliente(Long idCliente);    
    void save(BankAccount cuenta);
    void update(BankAccount cuenta);
    void delete(Long id);
    boolean existsById(Long idCuenta);

}