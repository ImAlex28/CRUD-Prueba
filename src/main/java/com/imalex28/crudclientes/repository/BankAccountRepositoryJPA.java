package com.imalex28.crudclientes.repository;

import java.util.List;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Named;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;

import com.imalex28.crudclientes.model.BankAccount;

@ApplicationScoped
@Named("jpa-cuenta")
public class BankAccountRepositoryJPA implements BankAccountRepository{
	
	@PersistenceContext
	EntityManager em;
	
	@Override
	public List<BankAccount> findAll(){
		return em.createQuery("SELECT c FROM BankAccount c", BankAccount.class).getResultList();
	}
	
	@Override
	public BankAccount findById(Long id) {
		return em.find(BankAccount.class, id);
	}
	
	@Override
	@Transactional
	public void save(BankAccount cuenta) {
		em.persist(cuenta);
	}
	
    @Override
    @Transactional
    public void update(BankAccount cuenta) {
        em.merge(cuenta);
    }
    
    @Override
    @Transactional
    public void delete(Long id) {
    	BankAccount cuenta = em.find(BankAccount.class, id);
        em.remove(cuenta);
    }

	@Override
	public List<BankAccount> findByIdCliente(Long idCliente) {
		return em.createQuery("SELECT c FROM BankAccount c WHERE c.client.clientId = :idCliente", BankAccount.class).setParameter("idCliente", idCliente).getResultList();
	}

	@Override
	public Double getSaldoTotalByCliente(Long idCliente) {
		Double total = em.createQuery("SELECT SUM(c.balance) FROM BankAccount c WHERE c.client.clientId = :idCliente", Double.class).setParameter("idCliente", idCliente).getSingleResult();
	    return total != null ? total : 0.0;
	}
	
	@Override
	public boolean existsById(Long idCuenta) {
	    return em.createQuery(
	            "SELECT COUNT(c) > 0 FROM BankAccount c WHERE c.bankAccountId = :id",
	            Boolean.class)
	        .setParameter("id", idCuenta)
	        .getSingleResult();
	}

}


/*

métodos JPA

em.createQuery(...) Ejecuta JPQL (SQL orientado a objetos)
em.find(Cliente.class, id) Busca por clave primaria 
em.persist(cliente) Inserta un nuevo registro 
em.merge(cliente) Actualiza un registro existente 
em.remove(cliente) Elimina un registro 

@Transactional es obligatorio para operaciones de escritura (insert, update, delete)
**/
