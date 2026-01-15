
package com.imalex28.crudclientes.unit.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.imalex28.crudclientes.controller.countries.CountriesController;
import com.imalex28.crudclientes.dto.country.CountryResponseDTO;
import com.imalex28.crudclientes.service.countries.CountryCacheService;

import jakarta.ws.rs.core.Response;

@ExtendWith(MockitoExtension.class)
public class CountriesControllerTest {

    @Mock
    CountryCacheService countryService;

    @InjectMocks
    CountriesController controller;

    @Test
    void getCountries_devuelve_lista_desde_service() {
        // Arrange
        CountryResponseDTO dto1 = mock(CountryResponseDTO.class);
        CountryResponseDTO dto2 = mock(CountryResponseDTO.class);
        List<CountryResponseDTO> esperado = List.of(dto1, dto2);
        when(countryService.listCountries()).thenReturn(esperado);

        // Act
        List<CountryResponseDTO> result = controller.getCountries();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(dto1, result.get(0));
        assertEquals(dto2, result.get(1));

        verify(countryService, times(1)).listCountries();
        verifyNoMoreInteractions(countryService);
    }

    @Test
    void getCountries_devuelve_lista_vacia_cuando_service_devuelve_vacio() {
        // Arrange
        when(countryService.listCountries()).thenReturn(List.of());

        // Act
        List<CountryResponseDTO> result = controller.getCountries();

        // Assert
        assertNotNull(result);
        assertEquals(0, result.size());

        verify(countryService, times(1)).listCountries();
        verifyNoMoreInteractions(countryService);
    }

    @Test
    void invalidateAllCache_devuelve_204_y_llama_service() {
        // Act
        Response resp = controller.invalidateAllCache();

        // Assert
        assertNotNull(resp);
        assertEquals(204, resp.getStatus());

        verify(countryService, times(1)).invalidateAll();
        verifyNoMoreInteractions(countryService);
    }

    @Test
    void invalidateAllCache_si_service_falla_se_propaga_excepcion() {
        // Arrange
        doThrow(new RuntimeException("Redis down")).when(countryService).invalidateAll();

        // Act & Assert
        RuntimeException ex = assertThrows(RuntimeException.class, () -> controller.invalidateAllCache());
        assertEquals("Redis down", ex.getMessage());

        verify(countryService, times(1)).invalidateAll();
        verifyNoMoreInteractions(countryService);
    }
}
