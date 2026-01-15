package com.imalex28.crudclientes.service.ip;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import com.imalex28.crudclientes.apiclient.IPApiClient;
import com.imalex28.crudclientes.dto.ip.ExternalIPDTO;
import com.imalex28.crudclientes.dto.ip.IPResponseDTO;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class IpCacheService {

    @Inject
    @RestClient
    IPApiClient client;

    @ConfigProperty(name = "ip.cache.ttl.millis", defaultValue = "6000")
	private
    long ttlMillis;

    private final Map<String, CacheEntry<IPResponseDTO>> cache = new ConcurrentHashMap<>();


    /**
     * Obtiene información por IP con caché y TTL:
     * - Si hay valor y no ha expirado → hit (devuelve caché).
     * - Si no existe o expiró → miss (llama a API y refresca).
     */
    public IPResponseDTO getIP(String ip, String token) {
        if (ip == null || ip.isBlank()) {
            throw new IllegalArgumentException("ip no puede ser null/blank");
        }

        String key = ip.trim();
        long now = System.currentTimeMillis();
        CacheEntry<IPResponseDTO> entry = cache.get(key);

        // HIT dentro del TTL
        if (entry != null && !entry.isExpired(now)) {
            return entry.value();
        }

        // MISS → llamada a la API externa
        ExternalIPDTO externalResult = client.getIP(ip, token);

        IPResponseDTO ipResponse = new IPResponseDTO();
        ipResponse.setIp(externalResult.getIp());
        ipResponse.setCountry(externalResult.getCountry());
        ipResponse.setAs_name(externalResult.getAs_name());
        ipResponse.setAs_domain(externalResult.getAs_domain());

        // Guardar en caché con nueva expiración
        cache.put(key, new CacheEntry<>(ipResponse, System.currentTimeMillis() + getTtlMillis()));
        return ipResponse;
    }

    
    public void invalidateAll() {
        cache.clear();
    }

    public long getTtlMillis() {
		return ttlMillis;
	}


	public void setTtlMillis(long ttlMillis) {
		this.ttlMillis = ttlMillis;
	}

	private static final class CacheEntry<T> {
        private final T value;
        private final long expiryMillis;

        CacheEntry(T value, long expiryMillis) {
            this.value = value;
            this.expiryMillis = expiryMillis;
        }

        T value() { return value; }
        boolean isExpired(long now) { return now >= expiryMillis; }
    }

}
