
package com.imalex28.crudclientes.unit.repository;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.imalex28.crudclientes.model.Client;
import com.imalex28.crudclientes.model.BankAccount;
import com.imalex28.crudclientes.repository.BankAccountRepositoryJPA;

import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;

@QuarkusTest
class CuentaRepositoryJPATest {
	
  @Inject	
  BankAccountRepositoryJPA repo;

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

  private BankAccount nuevaCuenta(Client cliente, String num, String tipo, double saldo) {
	BankAccount cta = new BankAccount();
    cta.setClient(cliente);  
    cta.setAccountNumber(num);
    cta.setAccountType(tipo);
    cta.setBalance(saldo);
    return cta;
  }

  // ---------- findAll ----------
  @Test
  @Transactional
  void findAll_devuelveTodasLasCuentas() {
    Client cli = nuevoCliente("Alex");
    em.persist(cli);

    em.persist(nuevaCuenta(cli,"ES00-101", "AHORRO", 100.0));
    em.persist(nuevaCuenta(cli,"ES00-102", "CORRIENTE", 200.0));

    em.flush();

    List<BankAccount> cuentas = repo.findAll();
    assertEquals(2, cuentas.size());
  }

  // ---------- findById ----------
  @Test
  @Transactional
  void findById_devuelveLaCuenta() {
    Client cli = nuevoCliente("Bea");
    em.persist(cli);

    BankAccount c = nuevaCuenta(cli,"ES00-201", "AHORRO", 50.0);
    em.persist(c);
    em.flush();  

    BankAccount encontrada = repo.findById(c.getBankAccountId());
    assertNotNull(encontrada);
    assertEquals("ES00-201", encontrada.getAccountNumber());
    assertEquals("AHORRO", encontrada.getAccountType());
    assertEquals(50.0, encontrada.getBalance());
  }

  // ---------- save ----------
  @Test
  @Transactional
  void save_persisteNuevaCuenta() {
    Client cli = nuevoCliente("Carlos");
    em.persist(cli);

    BankAccount nueva = nuevaCuenta(cli,"ES00-301", "CORRIENTE", 300.0);
    // si id autogenerado, deja id en null
    nueva.setBankAccountId(null);

    repo.save(nueva);
    em.flush();

    // Busca por número (o por id) para verificar
    List<BankAccount> all = repo.findAll();
    assertTrue(all.stream().anyMatch(c -> "ES00-301".equals(c.getAccountNumber())));
  }

  // ---------- update ----------
  @Test
  @Transactional
  void update_merge_actualizaCampos() {
    Client cli = nuevoCliente("Dani");
    em.persist(cli);

    BankAccount c = nuevaCuenta(cli, "ES00-401", "AHORRO", 400.0);
    em.persist(c);
    em.flush();

    // Cambiar saldo y tipo
    c.setBalance(450.0);
    c.setAccountType("PLAZO FIJO");

    repo.update(c);  // usa merge

    em.flush();
    em.clear();

    // Releer para confirmar cambios
    BankAccount reloaded = repo.findById(c.getBankAccountId());
    assertEquals(450.0, reloaded.getBalance());
    assertEquals("PLAZO FIJO", reloaded.getAccountType());
  }

  // ---------- delete ----------
  @Test
  @Transactional
  void delete_eliminaPorId() {
    Client cli = nuevoCliente("Eva");
    em.persist(cli);

    BankAccount c = nuevaCuenta(cli, "ES00-501", "AHORRO", 500.0);
    em.persist(c);
    em.flush();

    Long id = c.getBankAccountId();
    assertNotNull(id);

    repo.delete(id);
    em.flush();
    em.clear();

    BankAccount borrada = repo.findById(id);
    assertNull(borrada); // em.find devuelve null si no existe
  }

  // ---------- findByIdCliente ----------
  @Test
  @Transactional
  void findByIdCliente_filtraPorCliente() {
    Client cli1 = nuevoCliente("Fede");
    Client cli2 = nuevoCliente("Gina");
    em.persist(cli1);
    em.persist(cli2);

    em.persist(nuevaCuenta(cli1, "ES00-601", "AHORRO", 10.0));
    em.persist(nuevaCuenta(cli1, "ES00-602", "CORRIENTE", 20.0));
    em.persist(nuevaCuenta(cli2, "ES00-701", "AHORRO", 30.0));
    em.flush();

    List<BankAccount> cuentasCli1 = repo.findByIdCliente(cli1.getClientId());
    assertEquals(2, cuentasCli1.size());
    assertTrue(cuentasCli1.stream().allMatch(c -> c.getClient().getClientId().equals(cli1.getClientId())));

    List<BankAccount> cuentasCli2 = repo.findByIdCliente(cli2.getClientId());
    assertEquals(1, cuentasCli2.size());
    assertEquals("ES00-701", cuentasCli2.get(0).getAccountNumber());
  }

  // ---------- getSaldoTotalByCliente ----------
  @Test
  @Transactional
  void getSaldoTotalByCliente_sumaSaldos_oDevuelveCeroSiNoHayCuentas() {
    Client cli = nuevoCliente("Hugo");
    em.persist(cli);

    // Caso con cuentas
    em.persist(nuevaCuenta(cli, "ES00-801", "AHORRO", 100.0));
    em.persist(nuevaCuenta(cli, "ES00-802", "AHORRO", 150.0));
    em.flush();

    Double total = repo.getSaldoTotalByCliente(cli.getClientId());
    assertEquals(250.0, total);

    // Caso sin cuentas: SUM(...) devuelve null -> método debe retornar 0.0
    Client cli2 = nuevoCliente("Iris");
    em.persist(cli2);
    em.flush();

    Double totalVacio = repo.getSaldoTotalByCliente(cli2.getClientId());
    assertEquals(0.0, totalVacio);
  }

  // ---------- existsById ----------
  @Test
  @Transactional
  void existsById_trueSiExiste_falseSiNo() {
    Client cli = nuevoCliente("Juan");
    em.persist(cli);

    BankAccount c = nuevaCuenta(cli, "ES00-1001", "AHORRO", 1.0);
    em.persist(c);
    em.flush();

    assertTrue(repo.existsById(c.getBankAccountId()));
    assertFalse(repo.existsById(-1L));
  }
}
