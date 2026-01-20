
package com.imalex28.crudclientes.unit.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import java.sql.Date;
import java.time.LocalDate;
import java.time.ZoneId;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.imalex28.crudclientes.model.Task;
import com.imalex28.crudclientes.repository.ClientRepository;
import com.imalex28.crudclientes.repository.TaskRepository;
import com.imalex28.crudclientes.service.ClientCleanupService;

@ExtendWith(MockitoExtension.class)
public class ClientCleanupServiceTest {

  @Mock ClientRepository clientRepository;
  @Mock TaskRepository taskRepository;

  @InjectMocks ClientCleanupService service;

  // ---------- deleteOldUsers: usa cutoff correcto y delega en repo ----------
  @Test
  void deleteOldUsers_calculaCutoffYLlamaAlRepo() {
    // Arrange
    int retentionDays = 30;
    ZoneId tz = ZoneId.systemDefault();
    LocalDate expectedCutoffLd = LocalDate.now(tz).minusDays(retentionDays);
    Date expectedCutoff = Date.valueOf(expectedCutoffLd);

    ArgumentCaptor<Date> captor = ArgumentCaptor.forClass(Date.class);

    // Act
    service.deleteOldUsers(retentionDays);

    // Assert
    verify(clientRepository, times(1)).deleteOlderThan(captor.capture());
    verifyNoMoreInteractions(clientRepository, taskRepository);

    Date passed = captor.getValue();
    assertNotNull(passed, "El cutoff pasado al repositorio no debe ser null");
    assertEquals(expectedCutoff, passed, "El cutoff debe ser la fecha esperada (startOfDay del día local)");
  }

  // ---------- deleteOldUsers: límite retentionDays=0 (hoy) ----------
  @Test
  void deleteOldUsers_retentionZero_usaFechaDeHoy() {
    // Arrange
    int retentionDays = 0;
    ZoneId tz = ZoneId.systemDefault();
    LocalDate expectedCutoffLd = LocalDate.now(tz).minusDays(retentionDays);
    Date expectedCutoff = Date.valueOf(expectedCutoffLd);

    ArgumentCaptor<Date> captor = ArgumentCaptor.forClass(Date.class);

    // Act
    service.deleteOldUsers(retentionDays);

    // Assert
    verify(clientRepository, times(1)).deleteOlderThan(captor.capture());
    verifyNoMoreInteractions(clientRepository, taskRepository);

    Date passed = captor.getValue();
    assertNotNull(passed);
    assertEquals(expectedCutoff, passed, "Con retentionDays=0, el cutoff debe ser la fecha de hoy");
  }

  // ---------- saveTask: delega en taskRepository ----------
  @Test
  void saveTask_delegaEnRepositorio() {
    // Arrange
    Task t = new Task();

    // Act
    service.saveTask(t);

    // Assert
    verify(taskRepository, times(1)).save(t);
    verifyNoMoreInteractions(taskRepository, clientRepository);
  }
}
