package com.imalex28.crudclientes.repository;

import com.imalex28.crudclientes.model.Task;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Named;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;

@ApplicationScoped
@Named("jpa-task")
public class TaskRepositoryJPA implements TaskRepository {
	
	@PersistenceContext
	EntityManager em;
	
	@Override
	@Transactional
	public void save(Task task) {
		em.persist(task);
	}
}
