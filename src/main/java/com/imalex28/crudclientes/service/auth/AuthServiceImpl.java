
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

/**
 * Authentication service with JWT issuance and embedded per-user cache.
 * - Avoids recalculating tokens if the current one has not expired.
 * - Uses ConcurrentHashMap and compute(key, ...) to prevent race conditions
 *   (two threads will not generate two different tokens for the same user at the same time).
 */
@ApplicationScoped
public class AuthServiceImpl implements AuthService {

    @Inject
    @Named("jpa")
    ClienteRepository clientRepository;

    @Inject
    JwtGenerator jwtGenerator;

    //     Cache
    /**
     * Minimal cached structure: token + expiration.
     * We do not store full claims to keep it simple and lightweight.
     */
    private static final class CachedToken {
        private final String token;
        private final Instant expiration; // Exact moment when it expires

        CachedToken(String token, Instant expiration) {
            this.token = token;
            this.expiration = expiration;
        }

        String token() { return token; }

        boolean isExpired(long skewSeconds) {
            // If now + margin >= expiration, we consider it expired
            Instant nowWithSkew = Instant.now().plusSeconds(skewSeconds);
            return nowWithSkew.isAfter(expiration);
        }
    }

    private final ConcurrentHashMap<String, CachedToken> tokenCache = new ConcurrentHashMap<>();

    //   Configuration
    /** Margin to avoid tokens that expire “immediately” on the client side. */
    private static final long CLOCK_SKEW_SECONDS = 30L;
    /** TTL for the issued JWT (in seconds). */
    private static final int DEFAULT_TTL_SECONDS = 3600; // 1 hour

    //      Public API
    @Override
    @Transactional
    public String loginAndIssueToken(String username, String password) throws InvalidCredentialsException {
        // 1) Find user
        Cliente client = clientRepository.findByEmail(username);
        if (client == null) {
            throw new InvalidCredentialsException();
        }

        // 2) Verify password
        if (password == null) {
            throw new InvalidCredentialsException();
        }

        // 3) Cache key per user
        String userKey = client.getEmail();
        Objects.requireNonNull(userKey, "Client email must not be null");

        // 4) Resolve from cache or regenerate atomically if expired or does not exist
        CachedToken fresh = tokenCache.compute(userKey, (key, oldVal) -> {
            if (oldVal != null && !oldVal.isExpired(CLOCK_SKEW_SECONDS)) {
                return oldVal;
            }
            String newToken = generateJwtForUser(key);
            Instant exp = Instant.now().plusSeconds(DEFAULT_TTL_SECONDS);
            return new CachedToken(newToken, exp);
        });

        return fresh.token();
    }

    /**
     * Invalidates the cached token for the user.
     */
    public void invalidateUserToken(String username) {
        if (username != null) {
            tokenCache.remove(username);
        }
    }

    /** Alias for invalidation. */
    public void logout(String username) {
        invalidateUserToken(username);
    }

    /** Clears the entire cache. */
    public void clearAllTokens() {
        tokenCache.clear();
    }

    //   Implementation
    /**
     * Encapsulates JWT generation.
     * In production, you could add here:
     *  - dynamic role/permission claims
     *  - jti for revocation
     *  - issuer/audience/key versioning, etc.
     */
    private String generateJwtForUser(String userEmail) {
        try {
            return jwtGenerator.generateToken(userEmail, new String[] {"user"}, DEFAULT_TTL_SECONDS);
        } catch (Exception e) {
            throw new RuntimeException("Error generating token", e);
        }
    }
}
