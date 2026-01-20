package com.imalex28.crudclientes.unit.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.imalex28.crudclientes.model.Client;
import com.imalex28.crudclientes.repository.ClientRepositoryJPA;

import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;

@QuarkusTest
public class ClienteRepositoryJPATest {
	
	  @Inject	
	  ClientRepositoryJPA repo;

	  @Inject
	  EntityManager em;
	  
	  @BeforeEach
	  @Transactional
	  void setUp() throws Exception {
	    // limpiar datos
	    em.createQuery("DELETE FROM BankAccount").executeUpdate();
	    em.createQuery("DELETE FROM Client").executeUpdate();
	  }
	  
	  // Utilidades para crear datos de prueba
	  private Client nuevoCliente(String nombre) {
	    Client c = new Client();   
	    c.setName(nombre);
	    c.setSurname("Test");
	    c.setEmail(nombre.toLowerCase() + "@test.local");
	    return c;
	  }
	  
	  private Client nuevoCliente(String nombre, Date of) {
		    Client c = new Client();   
		    c.setName(nombre);
		    c.setSurname("Test");
		    c.setEmail(nombre.toLowerCase() + "@test.local");
		    c.setRegisterDate(of);
		    return c;
	  }
	  
	// ---------- findAll ----------
	  @Test
	  @Transactional
	  void findAll_devuelveTodosLosClientes() {
	    Client c1 = nuevoCliente("Alex");
	    Client c2 = nuevoCliente("Jesús");
	    em.persist(c1);
	    em.persist(c2);

	    em.flush();

	    List<Client> clientes = repo.findAll();
	    assertEquals(2, clientes.size());
	  }
	  
		// ---------- findById ----------
	  @Test
	  @Transactional
	  void findById_devuelveCliente() {
	    Client c1 = nuevoCliente("Alex");
	    em.persist(c1);

	    em.flush();

	    Client cliente = repo.findById(c1.getClientId());
	    assertNotNull(cliente);
	    assertEquals(c1.getClientId(), cliente.getClientId());
	    assertEquals(c1,cliente);
	  }
	  
	  @Test
	  @Transactional
	  void save_guardaCliente() {
		  Client c1 = nuevoCliente("Alex");
  
		  repo.save(c1);
		  
		  Client resultado = repo.findById(c1.getClientId());
		  assertNotNull(resultado);
		  assertEquals(c1,resultado);
	  }
	  
	  @Test
	  @Transactional
	  void update_actualizaCliente() {
		  Client c1 = nuevoCliente("Alex");
  
		  em.persist(c1);
		  
		  c1.setName("AlexUpdated");
		  repo.update(c1);
		  
		  Client resultado = repo.findById(c1.getClientId());
		  assertNotNull(resultado);
		  assertEquals("AlexUpdated",resultado.getName());
		  assertEquals(c1.getClientId(),resultado.getClientId());
	  }
	  
	  @Test
	  @Transactional
	  void delete_borraCliente() {
		  Client c1 = nuevoCliente("Alex");
  
		  em.persist(c1);
		  
		  repo.delete(c1.getClientId());
		  
		  Client resultado = repo.findById(c1.getClientId());
		  assertNull(resultado);
	  }
	  
	  // ---------- existsById ----------
	  @Test
	  @Transactional
	  void existsById_trueSiExiste_falseSiNo() {
	    Client cli = nuevoCliente("Juan");
	    em.persist(cli);

	    em.flush();

	    assertTrue(repo.existsById(cli.getClientId()));
	    assertFalse(repo.existsById(-1L));
	  }
	  
	  // ---------- findByEmail -----------
	  @Test
	  @Transactional
	  void findByEmail_devuelve_cliente(){
		  Client client = nuevoCliente("Juan");
		  client.setEmail("client@example.com");
		  em.persist(client);
		  em.flush();
		  
		  Client result = repo.findByEmail(client.getEmail());
		  
		  assertNotNull(result);
		  assertEquals(result.getName(),client.getName());
		  assertEquals(result.getEmail(),client.getEmail());
	  }
	  
	  // ---------- findByEmail Not Found -----------
	  @Test
	  @Transactional
	  void findByEmail_not_found(){
		  Client client = nuevoCliente("Juan");
		  client.setEmail("client@example.com");
		  String email = "client2@example.com";
		  em.persist(client);
		  em.flush();
		  
		  Client result = repo.findByEmail(email);
		  
		  assertNull(result);
	  }
	  
	// ---------- findByEmail Not Found -----------
		  @Test
		  @Transactional
		  void findByEmail_null_email(){
			  Client client = nuevoCliente("Juan");
			  client.setEmail("client@example.com");
			  String email = null;
			  em.persist(client);
			  em.flush();
			  
			  Client result = repo.findByEmail(email);
			  
			  assertNull(result);
		  }
		  

		// ---------- deleteOlderThan: borra los estrictamente anteriores al cutoff ----------
		  @Test
		  @Transactional
		  void deleteOlderThan_borraEstrictamenteMenoresQueCutoff() {
		    // Datos: 2023-01-01, 2024-01-01, 2025-01-01
		    Client c1 = nuevoCliente("c1", Date.valueOf(LocalDate.of(2023, 1, 1)));
		    Client c2 = nuevoCliente("c2", Date.valueOf(LocalDate.of(2024, 1, 1)));
		    Client c3 = nuevoCliente("c3", Date.valueOf(LocalDate.of(2025, 1, 1)));

		    em.persist(c1);
		    em.persist(c2);
		    em.persist(c3);
		    em.flush();

		    // Cutoff: 2024-06-01 → borra c1 (2023-01-01) y c2 (2024-01-01). Mantiene c3.
		    Date cutoff = Date.valueOf(LocalDate.of(2024, 6, 1));

		    int deleted = repo.deleteOlderThan(cutoff);
		    em.flush();
		    // Verifica borrados
		    assertEquals(2, deleted, "Deben borrarse exactamente 2 registros anteriores al cutoff");

		    Long remaining = em.createQuery("SELECT COUNT(c) FROM Client c", Long.class)
		                       .getSingleResult();
		    assertEquals(1L, remaining, "Debe quedar exactamente 1 registro");

		    Client survivor = em.createQuery("SELECT c FROM Client c", Client.class)
		                         .getSingleResult();
		    assertNotNull(survivor);

		    assertEquals(Date.valueOf(LocalDate.of(2025, 1, 1)), survivor.getRegisterDate());

		  }

		  // ---------- deleteOlderThan: no borra los iguales al cutoff (solo < cutoff) ----------
		  @Test
		  @Transactional
		  void deleteOlderThan_noBorraIgualesAlCutoff() {
		    Client c1 = nuevoCliente("c1", Date.valueOf(LocalDate.of(2024, 6, 1))); // igual a cutoff
		    Client c2 = nuevoCliente("c2", Date.valueOf(LocalDate.of(2024, 6, 2))); // mayor

		    em.persist(c1);
		    em.persist(c2);
		    em.flush();

		    Date cutoff = Date.valueOf(LocalDate.of(2024, 6, 1));

		    int deleted = repo.deleteOlderThan(cutoff);
		    em.flush();

		    assertEquals(0, deleted, "No debe borrar registros con fecha igual al cutoff ni superiores");

		    Long remaining = em.createQuery("SELECT COUNT(c) FROM Client c", Long.class)
		                       .getSingleResult();
		    assertEquals(2L, remaining, "Deben permanecer 2 registros");
		  }

		  // ---------- deleteOlderThan: no borra cuando todos son posteriores ----------
		  @Test
		  @Transactional
		  void deleteOlderThan_sinBorrados_siTodosSonPosteriores() {
		    Client c1 = nuevoCliente("c1", Date.valueOf(LocalDate.of(2025, 1, 1)));
		    Client c2 = nuevoCliente("c2", Date.valueOf(LocalDate.of(2025, 2, 1)));

		    em.persist(c1);
		    em.persist(c2);
		    em.flush();

		    Date cutoff = Date.valueOf(LocalDate.of(2024, 12, 31));

		    int deleted = repo.deleteOlderThan(cutoff);
		    em.flush();

		    assertEquals(0, deleted, "No debe borrar ningún registro (todos posteriores al cutoff)");

		    Long remaining = em.createQuery("SELECT COUNT(c) FROM Client c", Long.class)
		                       .getSingleResult();
		    assertEquals(2L, remaining);
		  }

}
