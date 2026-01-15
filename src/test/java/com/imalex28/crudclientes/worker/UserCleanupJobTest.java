package com.imalex28.crudclientes.worker;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.imalex28.crudclientes.model.Task;
import com.imalex28.crudclientes.service.ClientCleanupService;

@ExtendWith(MockitoExtension.class)
public class UserCleanupJobTest {

  @Mock
  ClientCleanupService cleanupService;

  @InjectMocks
  UserCleanupJob job;

  @BeforeEach
  void setUp() throws Exception {
    // Configura retentionDays mediante reflexión (simulando @ConfigProperty)
    setField(job, "retentionDays", 42);
  }

  // ---------- cleanUpOldUsers: crea Task y borra usuarios antiguos ----------
  @Test
  void cleanUpOldUsers_creaTaskYLlamaDeleteOldUsers() throws Exception {
    // Act: invoca cleanUpOldUsers() por reflexión (tiene visibilidad de paquete)
    invokePackageMethod(job, "cleanUpOldUsers");

    // Assert: se debe haber creado y guardado una Task
    ArgumentCaptor<Task> taskCaptor = ArgumentCaptor.forClass(Task.class);
    verify(cleanupService, times(1)).saveTask(taskCaptor.capture());
    Task saved = taskCaptor.getValue();
    assertNotNull(saved, "La Task creada no debe ser null");

    // Assert: se debe haber llamado a deleteOldUsers con retentionDays=42
    verify(cleanupService, times(1)).deleteOldUsers(42);

    // No más interacciones
    verifyNoMoreInteractions(cleanupService);
  }

  // ---------- createTask: guarda una Task ----------
  @Test
  void createTask_guardaTask() throws Exception {
    // Act: invoca createTask() por reflexión (tiene visibilidad de paquete)
    invokePackageMethod(job, "createTask");

    // Assert
    verify(cleanupService, times(1)).saveTask(any(Task.class));
    verifyNoMoreInteractions(cleanupService);
  }

  // ===== Utilidades de reflexión =====

  private static void setField(Object target, String fieldName, Object value) throws Exception {
    Field f = target.getClass().getDeclaredField(fieldName);
    f.setAccessible(true);
    f.set(target, value);
  }

  private static Object invokePackageMethod(Object target, String methodName, Class<?>... paramTypes) throws Exception {
    Method m = target.getClass().getDeclaredMethod(methodName, paramTypes);
    m.setAccessible(true);
    return m.invoke(target);
  }
}
