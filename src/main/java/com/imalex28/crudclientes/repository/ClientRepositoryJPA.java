package com.imalex28.crudclientes.repository;

import java.util.Date;
import java.util.List;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Named;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;

import com.imalex28.crudclientes.model.Client;

@ApplicationScoped
@Named("jpa")
public class ClientRepositoryJPA implements ClientRepository{
	
	@PersistenceContext
	EntityManager em;
	
	@Override
	public List<Client> findAll(){
		return em.createQuery("SELECT c FROM Client c", Client.class).getResultList();
	}
	
	@Override
	public Client findById(Long id) {
		return em.find(Client.class, id);
	}
	
	@Override
	@Transactional
	public void save(Client cliente) {
		em.persist(cliente);
	}
	
    @Override
    @Transactional
    public void update(Client cliente) {
        em.merge(cliente);
    }
    
    @Override
    @Transactional
    public void delete(Long id) {
        Client cliente = em.find(Client.class, id);
        em.remove(cliente);
    }
    
    @Override
	@Transactional
	public int deleteOlderThan(Date cutoff) {
	    return em.createQuery("DELETE FROM Client c WHERE c.registerDate < :cutoff")
	             .setParameter("cutoff", cutoff)
	             .executeUpdate();
	}
    
    @Override
    public boolean existsById(Long idCliente) {
        return !em.createQuery(
                "SELECT 1 FROM Client c WHERE c.clientId = :id")
            .setParameter("id", idCliente)
            .setMaxResults(1)
            .getResultList()
            .isEmpty();
    }
    
    @Override
    public Client findByEmail(String email) {
        try {
            return em.createQuery(
                    "SELECT c FROM Client c WHERE LOWER(c.email) = :email",
                    Client.class
                )
                .setParameter("email", email == null ? null : email.toLowerCase())
                .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
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
