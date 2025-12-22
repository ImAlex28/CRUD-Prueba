package com.imalex28.crudclientes.repository;

import java.util.List;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Named;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;

import com.imalex28.crudclientes.model.Cuenta;

@ApplicationScoped
@Named("jpa-cuenta")
public class CuentaRepositoryJPA implements CuentaRepository{
	
	@PersistenceContext
	EntityManager em;
	
	@Override
	public List<Cuenta> findAll(){
		return em.createQuery("SELECT c FROM Cuenta c", Cuenta.class).getResultList();
	}
	
	@Override
	public Cuenta findById(Long id) {
		return em.find(Cuenta.class, id);
	}
	
	@Override
	@Transactional
	public void save(Cuenta cuenta) {
		em.persist(cuenta);
	}
	
    @Override
    @Transactional
    public void update(Cuenta cuenta) {
        em.merge(cuenta);
    }
    
    @Override
    @Transactional
    public void delete(Long id) {
    	Cuenta cuenta = em.find(Cuenta.class, id);
        em.remove(cuenta);
    }

	@Override
	public List<Cuenta> findByIdCliente(Long idCliente) {
		return em.createQuery("SELECT c FROM Cuenta c WHERE c.cliente.idCliente = :idCliente", Cuenta.class).setParameter("idCliente", idCliente).getResultList();
	}

	@Override
	public Double getSaldoTotalByCliente(Long idCliente) {
		Double total = em.createQuery("SELECT SUM(c.saldo) FROM Cuenta c WHERE c.cliente.idCliente = :idCliente", Double.class).setParameter("idCliente", idCliente).getSingleResult();
	    return total != null ? total : 0.0;
	}
	
	@Override
	public boolean existsById(Long idCuenta) {
	    return em.createQuery(
	            "SELECT COUNT(c) > 0 FROM Cuenta c WHERE c.idCuenta = :id",
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
