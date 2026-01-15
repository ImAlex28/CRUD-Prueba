
package com.imalex28.crudclientes.controller.auth;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jwt.SignedJWT;
import com.nimbusds.jwt.JWTClaimsSet;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import jakarta.enterprise.context.ApplicationScoped;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.interfaces.RSAPrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.time.Instant;
import java.util.Base64;
import java.util.Date;

@ApplicationScoped
public class JwtGenerator {

    @ConfigProperty(name = "jwt.private.key.path")
    String privateKeyPath;

    @ConfigProperty(name = "mp.jwt.verify.issuer")
    String issuer;

    private RSAPrivateKey loadPrivateKey() throws Exception {
        // Reads the private key in PKCS#8 format (-----BEGIN PRIVATE KEY-----)
        String pem = Files.readString(Paths.get(privateKeyPath));
        String clean = pem
                .replace("-----BEGIN PRIVATE KEY-----", "")
                .replace("-----END PRIVATE KEY-----", "")
                .replaceAll("\\s", "");

        byte[] pkcs8 = Base64.getDecoder().decode(clean);
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(pkcs8);
        return (RSAPrivateKey) KeyFactory.getInstance("RSA").generatePrivate(spec);
    }


    /**
     * Generates an RS256 JWT with standard claims for Quarkus:
     * - sub: subject/user
     * - iss: issuer (must match mp.jwt.verify.issuer)
     * - groups: roles for @RolesAllowed
     * - iat, exp: issued at and expiration
     */
    public String generateToken(String subject, String[] groups, long ttlSeconds) throws Exception {
        RSAPrivateKey privateKey = loadPrivateKey();
        JWSSigner signer = new RSASSASigner(privateKey);

        Instant now = Instant.now();

        JWTClaimsSet claims = new JWTClaimsSet.Builder()
                .subject(subject)
                .issuer(issuer)
                .claim("groups", groups)
                .issueTime(Date.from(now))
                .expirationTime(Date.from(now.plusSeconds(ttlSeconds)))
                .build();

        SignedJWT jwt = new SignedJWT(
                new JWSHeader.Builder(JWSAlgorithm.RS256).type(JOSEObjectType.JWT).build(),
                claims
        );

        jwt.sign(signer);
        return jwt.serialize();
    }
}
