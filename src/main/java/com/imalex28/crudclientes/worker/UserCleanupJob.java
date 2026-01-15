package com.imalex28.crudclientes.worker;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import com.imalex28.crudclientes.model.Task;
import com.imalex28.crudclientes.service.ClientCleanupService;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import io.quarkus.scheduler.Scheduled;
import io.quarkus.scheduler.Scheduled.ConcurrentExecution;

@ApplicationScoped
public class UserCleanupJob {
	
	@Inject
	ClientCleanupService cleanupService;
	
	@ConfigProperty(name="clientCleanup.dasyToRetain")
	int retentionDays;
	

	@Scheduled(
	    cron = "${cron.userCleanUp.deleteOldUsers:off}",
	    identity = "JOB_CRUDPRUEBA_deleteOldUsers",
	    concurrentExecution = ConcurrentExecution.SKIP
	)
	void cleanUpOldUsers() {
	  createTask();
	  cleanupService.deleteOldUsers(retentionDays);
	}

	@Transactional
	void createTask() {
		Task task = new Task();
		cleanupService.saveTask(task);
	}
}
