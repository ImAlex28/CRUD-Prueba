package com.imalex28.crudclientes.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;

@Entity
@Table(name = "ONBOARDING_TASK")
public class Task {
	
	  @Id
	  @SequenceGenerator(name = "TASK_SEQ", allocationSize = 1, sequenceName = "TASK_SEQ")
	  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "TASK_SEQ")
	  @Column(name = "ID")
	  public Long id;
	  Instant createdAt;

	  public Task() {
	    createdAt = Instant.now();
	  }

	  public Task(Instant time) {
	    this.createdAt = time;
	  }

}
