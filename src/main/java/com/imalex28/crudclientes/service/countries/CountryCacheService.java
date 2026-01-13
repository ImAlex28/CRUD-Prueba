
package com.imalex28.crudclientes.service.countries;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.logging.Logger;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.imalex28.crudclientes.apiclient.CountriesApiClient;
import com.imalex28.crudclientes.dto.country.CountryResponseDTO;
import com.imalex28.crudclientes.dto.country.ExternalCountryDTO;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import io.quarkus.redis.datasource.RedisDataSource;
import io.quarkus.redis.datasource.keys.KeyCommands;
import io.quarkus.redis.datasource.value.ValueCommands;

@ApplicationScoped
public class CountryCacheService {

    private static final Logger LOG = Logger.getLogger(CountryCacheService.class);

    @Inject
    @RestClient
    CountriesApiClient client;

    @Inject
    RedisDataSource redis;

    @Inject
    ObjectMapper objectMapper;

    @ConfigProperty(name = "country.cache.ttl.millis", defaultValue = "300000")
    long ttlMillis;

    // Cambia el nombre si quieres versionar el formato
    private static final String CACHE_KEY = "countries:all:v1";

    private ValueCommands<String, String> valueCmds;
    private KeyCommands<String> keyCmds;

    @PostConstruct
    void init() {
        // Comandos tipados para String->String
        this.valueCmds = redis.value(String.class);
        this.keyCmds = redis.key();
    }

    public List<CountryResponseDTO> listCountries() {
        // 1) Intento de HIT en Redis
        String cachedJson = null;
        try {
            cachedJson = valueCmds.get(CACHE_KEY);
        } catch (Exception e) {
            // Si Redis está caído, no rompemos la request; solo log y seguimos como MISS
            LOG.warnf(e, "No se pudo leer la cache Redis para la key %s", CACHE_KEY);
        }

        if (cachedJson != null) {
            try {
                return objectMapper.readValue(cachedJson, new TypeReference<List<CountryResponseDTO>>() {});
            } catch (Exception e) {
                LOG.warnf(e, "Fallo deserializando JSON de la cache Redis para la key %s", CACHE_KEY);
                // Continuamos con MISS
            }
        }

        // 2) MISS → llamada a API externa
        String parameters = "languages,capital,name";
        List<ExternalCountryDTO> externalResult = client.getAll(parameters);

        List<CountryResponseDTO> countries = externalResult.stream()
            .map(e -> new CountryResponseDTO(
                e.name == null ? "Unknown" : e.name.common,
                (e.capitalCity == null || e.capitalCity.isEmpty()) ? "—" : e.capitalCity.get(0),
                e.languages == null ? List.of() : new ArrayList<>(e.languages.values())
            ))
            .collect(Collectors.toList());

        // 3) Guardar en Redis con TTL
        try {
            String json = objectMapper.writeValueAsString(countries);
            int ttlSeconds = (int) Math.max(1, ttlMillis / 1000); // SETEX usa segundos
            valueCmds.setex(CACHE_KEY, ttlSeconds, json);
        } catch (Exception e) {
            LOG.warnf(e, "No se pudo escribir en Redis la key %s", CACHE_KEY);
        }

        return countries;
    }

    public void invalidateAll() {
        try {
            keyCmds.del(CACHE_KEY);
        } catch (Exception e) {
            LOG.warnf(e, "No se pudo invalidar la cache Redis para la key %s", CACHE_KEY);
        }
    }
}
