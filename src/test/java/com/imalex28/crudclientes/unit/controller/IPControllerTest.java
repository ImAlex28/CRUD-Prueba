
package com.imalex28.crudclientes.unit.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.mock;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.imalex28.crudclientes.controller.ip.IPController;
import com.imalex28.crudclientes.dto.ip.IPResponseDTO;
import com.imalex28.crudclientes.service.ip.IpCacheService;

import jakarta.ws.rs.core.Response;

@ExtendWith(MockitoExtension.class)
public class IPControllerTest {

    @Mock
    IpCacheService ipService;

    @InjectMocks
    IPController controller;

    @Test
    void getCountries_devuelve_dto_del_service() {
        // Arrange
        String ip = "8.8.8.8";
        String token = "test-token";
        controller.setToken(token); // Inyección manual del @ConfigProperty en unit test

        IPResponseDTO esperado = mock(IPResponseDTO.class);
        when(ipService.getIP(ip, token)).thenReturn(esperado);

        // Act
        IPResponseDTO result = controller.getCountries(ip);

        // Assert
        assertNotNull(result);
        assertEquals(esperado, result);

        verify(ipService, times(1)).getIP(ip, token);
        verifyNoMoreInteractions(ipService);
    }

    @Test
    void getCountries_si_service_lanza_excepcion_se_propaga() {
        // Arrange
        String ip = "1.1.1.1";
        String token = "otro-token";
        controller.setToken(token);

        when(ipService.getIP(ip, token)).thenThrow(new RuntimeException("API caída"));

        // Act & Assert
        RuntimeException ex = assertThrows(RuntimeException.class, () -> controller.getCountries(ip));
        assertEquals("API caída", ex.getMessage());

        verify(ipService, times(1)).getIP(ip, token);
        verifyNoMoreInteractions(ipService);
    }

    @Test
    void invalidateAllCache_devuelve_204_y_llama_service() {
        // Act
        Response resp = controller.invalidateAllCache();

        // Assert
        assertNotNull(resp);
        assertEquals(204, resp.getStatus());

        verify(ipService, times(1)).invalidateAll();
        verifyNoMoreInteractions(ipService);
    }

    @Test
    void invalidateAllCache_si_service_falla_se_propaga_excepcion() {
        // Arrange
        doThrow(new RuntimeException("Redis no disponible")).when(ipService).invalidateAll();

        // Act & Assert
        RuntimeException ex = assertThrows(RuntimeException.class, () -> controller.invalidateAllCache());
        assertEquals("Redis no disponible", ex.getMessage());

        verify(ipService, times(1)).invalidateAll();
        verifyNoMoreInteractions(ipService);
    }
}
