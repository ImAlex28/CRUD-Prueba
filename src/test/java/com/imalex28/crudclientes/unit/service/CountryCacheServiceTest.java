
package com.imalex28.crudclientes.unit.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.imalex28.crudclientes.apiclient.CountriesApiClient;
import com.imalex28.crudclientes.dto.country.CountryResponseDTO;
import com.imalex28.crudclientes.dto.country.ExternalCountryDTO;
import com.imalex28.crudclientes.service.countries.CountryCacheService;

import io.quarkus.redis.datasource.RedisDataSource;
import io.quarkus.redis.datasource.keys.KeyCommands;
import io.quarkus.redis.datasource.value.ValueCommands;

@ExtendWith(MockitoExtension.class)
public class CountryCacheServiceTest {

    @Mock
    CountriesApiClient client;

    @Mock
    RedisDataSource redis;

    @Mock
    ValueCommands<String, String> valueCmds;

    @Mock
    KeyCommands<String> keyCmds;

    @InjectMocks
    CountryCacheService service;

    @BeforeEach
    void setUp() {
        // Inyectamos manualmente ObjectMapper real y TTL
        service.setObjectMapper(new ObjectMapper());
        service.setTtlMillis(300_000L); // 5 minutos

        // Configuramos RedisDataSource para devolver comandos tipados
        when(redis.value(String.class)).thenReturn(valueCmds);
        when(redis.key()).thenReturn(keyCmds);

        // Inicializa valueCmds / keyCmds como en @PostConstruct
        service.init();
    }

    // ---------- listCountries: HIT en caché ----------
    @Test
    void listCountries_hitEnRedis_devuelveListaDeserializada() throws Exception {
        // Arrange: preparamos JSON cacheado
        var cachedList = List.of(
            new CountryResponseDTO("Spain", "Madrid", List.of("Spanish")),
            new CountryResponseDTO("France", "Paris", List.of("French"))
        );
        String cachedJson = service.getObjectMapper().writeValueAsString(cachedList);

        when(valueCmds.get("countries:all:v1")).thenReturn(cachedJson);

        // Act
        List<CountryResponseDTO> result = service.listCountries();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Spain", result.get(0).getName());
        assertEquals("Madrid", result.get(0).getCapital());
        assertEquals(List.of("Spanish"), result.get(0).getLanguages());
        assertEquals("France", result.get(1).getName());

        verify(valueCmds, times(1)).get("countries:all:v1");
        verifyNoInteractions(client); // No debe llamar a la API si el caché es válido
    }

    // ---------- listCountries: MISS (sin caché) -> API + setex ----------
    @Test
    void listCountries_miss_llamaApi_mapeaYGuardaEnRedisConTTL() throws Exception {
        // Arrange
        when(valueCmds.get("countries:all:v1")).thenReturn(null);

        // Preparamos respuesta externa
        ExternalCountryDTO ex1 = externalCountry("Spain", "Madrid", Map.of("es", "Spanish"));
        ExternalCountryDTO ex2 = externalCountry("France", "Paris", Map.of("fr", "French"));
        when(client.getAll("languages,capital,name")).thenReturn(List.of(ex1, ex2));

        // TTL esperado en segundos (SETEX usa segundos)
        int expectedTtlSeconds = (int) Math.max(1, service.getTtlMillis() / 1000);

        // Act
        List<CountryResponseDTO> result = service.listCountries();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Spain", result.get(0).getName());
        assertEquals("Madrid", result.get(0).getCapital());
        assertEquals(List.of("Spanish"), result.get(0).getLanguages());
        assertEquals("France", result.get(1).getName());

        verify(valueCmds, times(1)).get("countries:all:v1");
        verify(client, times(1)).getAll("languages,capital,name");
        verify(valueCmds, times(1)).setex(eq("countries:all:v1"), eq((long) expectedTtlSeconds), anyString());
        verifyNoMoreInteractions(valueCmds, client);
    }

    // ---------- listCountries: JSON cacheado malformado -> MISS ----------
    @Test
    void listCountries_cacheConJsonMalformado_ignoraYCaeAMiss() {
        // Arrange: JSON inválido en caché
        when(valueCmds.get("countries:all:v1")).thenReturn("{ invalid json");

        ExternalCountryDTO ex = externalCountry("Italy", "Rome", Map.of("it", "Italian"));
        when(client.getAll("languages,capital,name")).thenReturn(List.of(ex));

        // Act
        List<CountryResponseDTO> result = service.listCountries();

        // Assert: se obtiene de API y se mapea
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Italy", result.get(0).getName());
        assertEquals("Rome", result.get(0).getCapital());
        assertEquals(List.of("Italian"), result.get(0).getLanguages());

        verify(valueCmds, times(1)).get("countries:all:v1");
        verify(client, times(1)).getAll("languages,capital,name");
    }

    // ---------- listCountries: fallo leyendo Redis -> MISS ----------
    @Test
    void listCountries_falloLecturaRedis_noRompe_yHaceMiss() {
        // Arrange: simulamos excepción al leer cache
        when(valueCmds.get("countries:all:v1"))
            .thenThrow(new RuntimeException("Redis down"));

        ExternalCountryDTO ex = externalCountry("Portugal", "Lisbon", Map.of("pt", "Portuguese"));
        when(client.getAll("languages,capital,name")).thenReturn(List.of(ex));

        // Act
        List<CountryResponseDTO> result = service.listCountries();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Portugal", result.get(0).getName());

        verify(valueCmds, times(1)).get("countries:all:v1");
        verify(client, times(1)).getAll("languages,capital,name");
    }

    // ---------- invalidateAll: borra la key ----------
    @Test
    void invalidateAll_borraClaveEnRedis() {
        // Act
        service.invalidateAll();

        // Assert
        verify(keyCmds, times(1)).del("countries:all:v1");
        verifyNoMoreInteractions(keyCmds);
    }

    // ---------- invalidateAll: fallo en Redis no lanza excepción ----------
    @Test
    void invalidateAll_falloEnRedis_noLanzaExcepcion() {
        doThrow(new RuntimeException("Redis not reachable"))
            .when(keyCmds).del("countries:all:v1");

        assertDoesNotThrow(() -> service.invalidateAll());
        verify(keyCmds, times(1)).del("countries:all:v1");
        verifyNoMoreInteractions(keyCmds);
    }
    
	@Test
	void listCountries_errorEscribiendoRedis_noLanzaExcepcion_yDevuelveLista() throws Exception {
	    // Arrange: cache MISS
	    when(valueCmds.get("countries:all:v1")).thenReturn(null);
	
	    // Respuesta externa de la API
	    ExternalCountryDTO ex1 = externalCountry("Spain", "Madrid", Map.of("es", "Spanish"));
	    ExternalCountryDTO ex2 = externalCountry("France", "Paris", Map.of("fr", "French"));
	    when(client.getAll("languages,capital,name")).thenReturn(List.of(ex1, ex2));
	
	    // Simulamos fallo al escribir en Redis (setex lanza excepción)
	    // Usa anyLong() porque el método espera long, no Integer.
	    doThrow(new RuntimeException("Redis write failed"))
	        .when(valueCmds)
	        .setex(eq("countries:all:v1"), anyLong(), anyString());
	
	    // Act
	    List<CountryResponseDTO> result = service.listCountries();
	
	    // Assert: el servicio NO propaga la excepción y devuelve la lista mapeada
	    assertNotNull(result);
	    assertEquals(2, result.size());
	    assertEquals("Spain", result.get(0).getName());
	    assertEquals("Madrid", result.get(0).getCapital());
	    assertEquals(List.of("Spanish"), result.get(0).getLanguages());
	    assertEquals("France", result.get(1).getName());
	    assertEquals("Paris", result.get(1).getCapital());
	    assertEquals(List.of("French"), result.get(1).getLanguages());
	
	    // Verificaciones de interacción:
	    verify(valueCmds, times(1)).get("countries:all:v1");
	    verify(client, times(1)).getAll("languages,capital,name");
	    verify(valueCmds, times(1)).setex(eq("countries:all:v1"), anyLong(), anyString());
	    verifyNoMoreInteractions(valueCmds, client);
	}


    // ===== Helper para crear ExternalCountryDTO coherente =====
    private static ExternalCountryDTO externalCountry(String nameCommon, String capital0, Map<String, String> languages) {
        ExternalCountryDTO dto = new ExternalCountryDTO();

        // dto.name.common (maneja null si se pasa nameCommon nulo)
        if (nameCommon != null) {
            ExternalCountryDTO.Name n = new ExternalCountryDTO.Name();
            n.common = nameCommon;
            dto.name = n;
        } else {
            dto.name = null;
        }

        // dto.capitalCity (lista con el primer elemento capital0 si no es null)
        if (capital0 != null) {
            dto.capitalCity = List.of(capital0);
        } else {
            dto.capitalCity = null;
        }

        // dto.languages (mapa tal cual)
        dto.languages = languages;

        return dto;
    }
}
