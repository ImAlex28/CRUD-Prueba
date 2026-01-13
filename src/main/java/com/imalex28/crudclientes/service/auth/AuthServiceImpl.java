
package com.imalex28.crudclientes.service.auth;

import com.imalex28.crudclientes.model.Cliente;
import com.imalex28.crudclientes.repository.ClienteRepository;
import com.imalex28.crudclientes.controller.auth.JwtGenerator;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.transaction.Transactional;

import java.time.Instant;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

@ApplicationScoped
public class AuthServiceImpl implements AuthService {

    @Inject
    @Named("jpa")
    ClienteRepository clientRepository;

    @Inject
    JwtGenerator jwtGenerator;

    // ======== Cache embebida ========
    // Key: username/email ; Value: token + exp
    private static final class CachedToken {
        private final String token;
        private final long expEpochSeconds; // claim exp en epoch seconds
        private CachedToken(String token, long expEpochSeconds) {
            this.token = token;
            this.expEpochSeconds = expEpochSeconds;
        }
        String getToken() { return token; }
        long getExpEpochSeconds() { return expEpochSeconds; }
        boolean isExpired(long skewSeconds) {
            long now = Instant.now().getEpochSecond();
            return now + skewSeconds >= expEpochSeconds;
        }
    }

    private final ConcurrentHashMap<String, CachedToken> tokenCache = new ConcurrentHashMap<>();

    // ======== Configuración ========
    private static final long CLOCK_SKEW_SECONDS = 30L;   // margen para evitar tokens a punto de expirar
    private static final int DEFAULT_TTL_SECONDS = 3600;  // 1h

    @Override
    @Transactional
    public String loginAndIssueToken(String username, String password) throws InvalidCredentialsException {
        // 1) Buscar usuario
        Cliente client = clientRepository.findByEmail(username);
        if (client == null) {
            throw new InvalidCredentialsException();
        }

        // 2) Verificar contraseña (educativo: aquí solo null-check; en real, hash + salt + timing-safe)
        if (password == null) {
            throw new InvalidCredentialsException();
        }

        String userKey = client.getEmail();
        Objects.requireNonNull(userKey, "El email del cliente no debe ser null");

        // 3) Intentar devolver token cacheado si sigue vigente
        CachedToken cached = tokenCache.get(userKey);
        if (cached != null && !cached.isExpired(CLOCK_SKEW_SECONDS)) {
            return cached.getToken();
        }

        // 4) Generar un nuevo token de forma segura (evitar carreras con compute)
        // compute() es atómico por key; así evitamos generar 2 tokens a la vez para el mismo usuario
        CachedToken fresh = tokenCache.compute(userKey, (key, oldVal) -> {
            // Si otro hilo ya lo regeneró y aún no expira, reutiliza
            if (oldVal != null && !oldVal.isExpired(CLOCK_SKEW_SECONDS)) {
                return oldVal;
            }
            try {
                String newToken = jwtGenerator.generateToken(key, new String[] { "user" }, DEFAULT_TTL_SECONDS);
                long exp = Instant.now().getEpochSecond() + DEFAULT_TTL_SECONDS;
                return new CachedToken(newToken, exp);
            } catch (Exception e) {
                // Si falla, no dejes un valor corrupto en caché (compute no pone nada si retornas null)
                throw new RuntimeException("Error generando token", e);
            }
        });

        return fresh.getToken();
    }

    // ======== Operaciones opcionales sobre la caché ========

    /** "Logout" educativo: invalida el token vigente en caché. */
    public void logout(String username) {
        if (username != null) {
            tokenCache.remove(username);
        }
    }

    /** Invalida el token cacheado cuando cambian credenciales/roles del usuario. */
    public void invalidateUserToken(String username) {
        if (username != null) {
            tokenCache.remove(username);
        }
    }

    /** Limpia toda la caché (útil en tests o rotación de claves de firma). */
    public void clearAllTokens() {
        tokenCache.clear();
    }
}
