package com.imalex28.crudclientes.service;

import java.time.LocalDate;
import java.time.ZoneId;

import com.imalex28.crudclientes.model.Task;
import com.imalex28.crudclientes.repository.ClienteRepository;
import com.imalex28.crudclientes.repository.TaskRepository;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

@ApplicationScoped
public class ClientCleanupService {
	
	@Inject
	@Named("jpa")
	ClienteRepository clientRepository;
	
	@Inject
	@Named("jpa-task")
	TaskRepository taskRepository;
	
	public void deleteOldUsers(int retentionDays) {
		
		//Calculate the date from which we want to delete the users.
		LocalDate cutoffLd = LocalDate.now(ZoneId.systemDefault()).minusDays(retentionDays);
		java.sql.Date cutoff = java.sql.Date.valueOf(cutoffLd);

		clientRepository.deleteOlderThan(cutoff);	
	}

	public void saveTask(Task task) {
		taskRepository.save(task);
	}
}
