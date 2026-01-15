package com.imalex28.crudclientes.unit.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.imalex28.crudclientes.model.Cliente;
import com.imalex28.crudclientes.repository.ClienteRepositoryJPA;

import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;

@QuarkusTest
public class ClienteRepositoryJPATest {
	
	  @Inject	
	  ClienteRepositoryJPA repo;

	  @Inject
	  EntityManager em;
	  
	  @BeforeEach
	  @Transactional
	  void setUp() throws Exception {
	    // limpiar datos
	    em.createQuery("DELETE FROM Cuenta").executeUpdate();
	    em.createQuery("DELETE FROM Cliente").executeUpdate();
	  }
	  
	  // Utilidades para crear datos de prueba
	  private Cliente nuevoCliente(String nombre) {
	    Cliente c = new Cliente();   
	    c.setNombre(nombre);
	    c.setApellidos("Test");
	    c.setEmail(nombre.toLowerCase() + "@test.local");
	    return c;
	  }
	  
	// ---------- findAll ----------
	  @Test
	  @Transactional
	  void findAll_devuelveTodosLosClientes() {
	    Cliente c1 = nuevoCliente("Alex");
	    Cliente c2 = nuevoCliente("Jesús");
	    em.persist(c1);
	    em.persist(c2);

	    em.flush();

	    List<Cliente> clientes = repo.findAll();
	    assertEquals(2, clientes.size());
	  }
	  
		// ---------- findById ----------
	  @Test
	  @Transactional
	  void findById_devuelveCliente() {
	    Cliente c1 = nuevoCliente("Alex");
	    em.persist(c1);

	    em.flush();

	    Cliente cliente = repo.findById(c1.getIdCliente());
	    assertNotNull(cliente);
	    assertEquals(c1.getIdCliente(), cliente.getIdCliente());
	    assertEquals(c1,cliente);
	  }
	  
	  @Test
	  @Transactional
	  void save_guardaCliente() {
		  Cliente c1 = nuevoCliente("Alex");
  
		  repo.save(c1);
		  
		  Cliente resultado = repo.findById(c1.getIdCliente());
		  assertNotNull(resultado);
		  assertEquals(c1,resultado);
	  }
	  
	  @Test
	  @Transactional
	  void update_actualizaCliente() {
		  Cliente c1 = nuevoCliente("Alex");
  
		  em.persist(c1);
		  
		  c1.setNombre("AlexUpdated");
		  repo.update(c1);
		  
		  Cliente resultado = repo.findById(c1.getIdCliente());
		  assertNotNull(resultado);
		  assertEquals("AlexUpdated",resultado.getNombre());
		  assertEquals(c1.getIdCliente(),resultado.getIdCliente());
	  }
	  
	  @Test
	  @Transactional
	  void delete_borraCliente() {
		  Cliente c1 = nuevoCliente("Alex");
  
		  em.persist(c1);
		  
		  repo.delete(c1.getIdCliente());
		  
		  Cliente resultado = repo.findById(c1.getIdCliente());
		  assertNull(resultado);
	  }
	  
	  // ---------- existsById ----------
	  @Test
	  @Transactional
	  void existsById_trueSiExiste_falseSiNo() {
	    Cliente cli = nuevoCliente("Juan");
	    em.persist(cli);

	    em.flush();

	    assertTrue(repo.existsById(cli.getIdCliente()));
	    assertFalse(repo.existsById(-1L));
	  }
	  
	  // ---------- findByEmail -----------
	  @Test
	  @Transactional
	  void findByEmail_devuelve_cliente(){
		  Cliente client = nuevoCliente("Juan");
		  client.setEmail("client@example.com");
		  em.persist(client);
		  em.flush();
		  
		  Cliente result = repo.findByEmail(client.getEmail());
		  
		  assertNotNull(result);
		  assertEquals(result.getNombre(),client.getNombre());
		  assertEquals(result.getEmail(),client.getEmail());
	  }
	  
	  // ---------- findByEmail Not Found -----------
	  @Test
	  @Transactional
	  void findByEmail_not_found(){
		  Cliente client = nuevoCliente("Juan");
		  client.setEmail("client@example.com");
		  String email = "client2@example.com";
		  em.persist(client);
		  em.flush();
		  
		  Cliente result = repo.findByEmail(email);
		  
		  assertNull(result);
	  }
	  
	// ---------- findByEmail Not Found -----------
		  @Test
		  @Transactional
		  void findByEmail_null_email(){
			  Cliente client = nuevoCliente("Juan");
			  client.setEmail("client@example.com");
			  String email = null;
			  em.persist(client);
			  em.flush();
			  
			  Cliente result = repo.findByEmail(email);
			  
			  assertNull(result);
		  }

}
