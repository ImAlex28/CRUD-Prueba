
package com.imalex28.crudclientes.unit.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.time.Instant;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.imalex28.crudclientes.model.Task;
import com.imalex28.crudclientes.repository.TaskRepositoryJPA;

import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;

@QuarkusTest
public class TaskRepositoryJPATest {

    @Inject
    TaskRepositoryJPA repo;

    @Inject
    EntityManager em;

    @BeforeEach
    @Transactional
    void setUp() {
        // Limpiar datos previos
        em.createQuery("DELETE FROM Task").executeUpdate();
    }

    // Utilidad para crear Tasks con o sin fecha
    private Task nuevaTask(Instant when) {
        return (when != null) ? new Task(when) : new Task();
    }

    // ---------- save: persiste una task ----------
    @Test
    @Transactional
    void save_guardaTask() {
        Task t = nuevaTask(Instant.parse("2023-01-15T00:00:00Z"));

        repo.save(t);
        em.flush();

        assertNotNull(t.id, "El ID debe asignarse tras persistir");

        Task fetched = em.find(Task.class, t.id);
        assertNotNull(fetched, "Debe poder recuperarse la Task por su ID");
    }

    // ---------- save: persiste múltiples tasks ----------
    @Test
    @Transactional
    void save_guardaMultiplesTasks() {
        Task t1 = nuevaTask(Instant.parse("2024-01-01T00:00:00Z"));
        Task t2 = nuevaTask(Instant.parse("2024-02-01T00:00:00Z"));

        repo.save(t1);
        repo.save(t2);
        em.flush();

        Long count = em.createQuery("SELECT COUNT(t) FROM Task t", Long.class)
                       .getSingleResult();

        assertEquals(2L, count, "Deben existir 2 registros en la tabla de Task");
    }

    // ---------- save: task con fecha por defecto (constructor por defecto) ----------
    @Test
    @Transactional
    void save_conFechaPorDefecto_persiste() {
        Task t = nuevaTask(null); // usa el constructor por defecto (Instant.now())

        repo.save(t);
        em.flush();

        assertNotNull(t.id, "El ID debe existir tras persistir");

        Task fetched = em.find(Task.class, t.id);
        assertNotNull(fetched, "La Task debe estar en la base de datos");
    }

    // ---------- save: IDs diferentes para tasks distintas ----------
    @Test
    @Transactional
    void save_idsDiferentes_paraTasksDiferentes() {
        Task t1 = nuevaTask(Instant.parse("2025-01-01T00:00:00Z"));
        Task t2 = nuevaTask(Instant.parse("2025-01-02T00:00:00Z"));

        repo.save(t1);
        repo.save(t2);
        em.flush();

        assertNotNull(t1.id);
        assertNotNull(t2.id);
        assertNotEquals(t1.id, t2.id, "Cada Task debe tener un ID distinto");
    }
}
