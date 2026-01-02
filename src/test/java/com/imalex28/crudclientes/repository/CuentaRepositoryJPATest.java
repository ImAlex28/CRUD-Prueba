
package com.imalex28.crudclientes.repository;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.imalex28.crudclientes.model.Cliente;
import com.imalex28.crudclientes.model.Cuenta;

import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;

@QuarkusTest
class CuentaRepositoryJPATest {
	
  @Inject	
  CuentaRepositoryJPA repo;

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

  private Cuenta nuevaCuenta(Cliente cliente, String num, String tipo, double saldo) {
    Cuenta cta = new Cuenta();
    cta.setIdCliente(cliente);  
    cta.setNumeroCuenta(num);
    cta.setTipoCuenta(tipo);
    cta.setSaldo(saldo);
    return cta;
  }

  // ---------- findAll ----------
  @Test
  @Transactional
  void findAll_devuelveTodasLasCuentas() {
    Cliente cli = nuevoCliente("Alex");
    em.persist(cli);

    em.persist(nuevaCuenta(cli,"ES00-101", "AHORRO", 100.0));
    em.persist(nuevaCuenta(cli,"ES00-102", "CORRIENTE", 200.0));

    em.flush();

    List<Cuenta> cuentas = repo.findAll();
    assertEquals(2, cuentas.size());
  }

  // ---------- findById ----------
  @Test
  @Transactional
  void findById_devuelveLaCuenta() {
    Cliente cli = nuevoCliente("Bea");
    em.persist(cli);

    Cuenta c = nuevaCuenta(cli,"ES00-201", "AHORRO", 50.0);
    em.persist(c);
    em.flush();  

    Cuenta encontrada = repo.findById(c.getIdCuenta());
    assertNotNull(encontrada);
    assertEquals("ES00-201", encontrada.getNumeroCuenta());
    assertEquals("AHORRO", encontrada.getTipoCuenta());
    assertEquals(50.0, encontrada.getSaldo());
  }

  // ---------- save ----------
  @Test
  @Transactional
  void save_persisteNuevaCuenta() {
    Cliente cli = nuevoCliente("Carlos");
    em.persist(cli);

    Cuenta nueva = nuevaCuenta(cli,"ES00-301", "CORRIENTE", 300.0);
    // si id autogenerado, deja id en null
    nueva.setIdCuenta(null);

    repo.save(nueva);
    em.flush();

    // Busca por número (o por id) para verificar
    List<Cuenta> all = repo.findAll();
    assertTrue(all.stream().anyMatch(c -> "ES00-301".equals(c.getNumeroCuenta())));
  }

  // ---------- update ----------
  @Test
  @Transactional
  void update_merge_actualizaCampos() {
    Cliente cli = nuevoCliente("Dani");
    em.persist(cli);

    Cuenta c = nuevaCuenta(cli, "ES00-401", "AHORRO", 400.0);
    em.persist(c);
    em.flush();

    // Cambiar saldo y tipo
    c.setSaldo(450.0);
    c.setTipoCuenta("PLAZO FIJO");

    repo.update(c);  // usa merge

    em.flush();
    em.clear();

    // Releer para confirmar cambios
    Cuenta reloaded = repo.findById(c.getIdCuenta());
    assertEquals(450.0, reloaded.getSaldo());
    assertEquals("PLAZO FIJO", reloaded.getTipoCuenta());
  }

  // ---------- delete ----------
  @Test
  @Transactional
  void delete_eliminaPorId() {
    Cliente cli = nuevoCliente("Eva");
    em.persist(cli);

    Cuenta c = nuevaCuenta(cli, "ES00-501", "AHORRO", 500.0);
    em.persist(c);
    em.flush();

    Long id = c.getIdCuenta();
    assertNotNull(id);

    repo.delete(id);
    em.flush();
    em.clear();

    Cuenta borrada = repo.findById(id);
    assertNull(borrada); // em.find devuelve null si no existe
  }

  // ---------- findByIdCliente ----------
  @Test
  @Transactional
  void findByIdCliente_filtraPorCliente() {
    Cliente cli1 = nuevoCliente("Fede");
    Cliente cli2 = nuevoCliente("Gina");
    em.persist(cli1);
    em.persist(cli2);

    em.persist(nuevaCuenta(cli1, "ES00-601", "AHORRO", 10.0));
    em.persist(nuevaCuenta(cli1, "ES00-602", "CORRIENTE", 20.0));
    em.persist(nuevaCuenta(cli2, "ES00-701", "AHORRO", 30.0));
    em.flush();

    List<Cuenta> cuentasCli1 = repo.findByIdCliente(cli1.getIdCliente());
    assertEquals(2, cuentasCli1.size());
    assertTrue(cuentasCli1.stream().allMatch(c -> c.getIdCliente().getIdCliente().equals(cli1.getIdCliente())));

    List<Cuenta> cuentasCli2 = repo.findByIdCliente(cli2.getIdCliente());
    assertEquals(1, cuentasCli2.size());
    assertEquals("ES00-701", cuentasCli2.get(0).getNumeroCuenta());
  }

  // ---------- getSaldoTotalByCliente ----------
  @Test
  @Transactional
  void getSaldoTotalByCliente_sumaSaldos_oDevuelveCeroSiNoHayCuentas() {
    Cliente cli = nuevoCliente("Hugo");
    em.persist(cli);

    // Caso con cuentas
    em.persist(nuevaCuenta(cli, "ES00-801", "AHORRO", 100.0));
    em.persist(nuevaCuenta(cli, "ES00-802", "AHORRO", 150.0));
    em.flush();

    Double total = repo.getSaldoTotalByCliente(cli.getIdCliente());
    assertEquals(250.0, total);

    // Caso sin cuentas: SUM(...) devuelve null -> método debe retornar 0.0
    Cliente cli2 = nuevoCliente("Iris");
    em.persist(cli2);
    em.flush();

    Double totalVacio = repo.getSaldoTotalByCliente(cli2.getIdCliente());
    assertEquals(0.0, totalVacio);
  }

  // ---------- existsById ----------
  @Test
  @Transactional
  void existsById_trueSiExiste_falseSiNo() {
    Cliente cli = nuevoCliente("Juan");
    em.persist(cli);

    Cuenta c = nuevaCuenta(cli, "ES00-1001", "AHORRO", 1.0);
    em.persist(c);
    em.flush();

    assertTrue(repo.existsById(c.getIdCuenta()));
    assertFalse(repo.existsById(-1L));
  }
}
