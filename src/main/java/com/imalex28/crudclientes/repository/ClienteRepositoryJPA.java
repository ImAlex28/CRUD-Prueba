package com.imalex28.crudclientes.repository;

import java.util.List;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Named;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;

import com.imalex28.crudclientes.model.Cliente;

@ApplicationScoped
@Named("jpa")
public class ClienteRepositoryJPA implements ClienteRepository{
	
	@PersistenceContext
	EntityManager em;
	
	@Override
	public List<Cliente> findAll(){
		return em.createQuery("SELECT c FROM Cliente c", Cliente.class).getResultList();
	}
	
	@Override
	public Cliente findById(Long id) {
		return em.find(Cliente.class, id);
	}
	
	@Override
	@Transactional
	public void save(Cliente cliente) {
		em.persist(cliente);
	}
	
    @Override
    @Transactional
    public void update(Cliente cliente) {
        em.merge(cliente);
    }
    
    @Override
    @Transactional
    public void delete(Long id) {
        Cliente cliente = em.find(Cliente.class, id);
        em.remove(cliente);
    }
    
    @Override
    public boolean existsById(Long idCliente) {
        return !em.createQuery(
                "SELECT 1 FROM Cliente c WHERE c.idCliente = :id")
            .setParameter("id", idCliente)
            .setMaxResults(1)
            .getResultList()
            .isEmpty();
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
