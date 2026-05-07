package com.bloodflow.auth.service;

import com.bloodflow.auth.config.JwtProperties;
import com.bloodflow.auth.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Handles JWT creation and validation.
 *
 * JWT = JSON Web Token.
 *
 * HOW IT WORKS:
 * 1. After login, we create a JWT with the user's data inside (claims).
 * 2. The JWT is signed with our secret key so nobody can fake it.
 * 3. The frontend sends the JWT in every request: Authorization: Bearer <token>
 * 4. Our filter validates the signature and reads the user data.
 *
 * SOLID note (SRP): This class does one thing — JWT operations.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class JwtService {

    private final JwtProperties jwtProperties;

    /**
     * Creates a JWT access token for the given user.
     *
     * The token contains:
     * - sub: user id (subject)
     * - email
     * - fullName
     * - roles: list of role names
     * - iss: issuer
     * - aud: audience
     * - iat: issued at
     * - exp: expiration time
     */
    public String generateAccessToken(User user) {
        List<String> roles = user.getRoles().stream()
            .map(role -> role.getName().name())
            .collect(Collectors.toList());

        long expirationMillis = jwtProperties.getAccessTokenExpirationMinutes() * 60 * 1000L;
        Date now = new Date();
        Date expiry = new Date(now.getTime() + expirationMillis);

        return Jwts.builder()
            .setSubject(String.valueOf(user.getId()))
            .claim("email", user.getEmail())
            .claim("fullName", user.getFullName())
            .claim("roles", roles)
            .setIssuer(jwtProperties.getIssuer())
            .setAudience(jwtProperties.getAudience())
            .setIssuedAt(now)
            .setExpiration(expiry)
            .signWith(getSigningKey(), SignatureAlgorithm.HS256)
            .compact();
    }

    /**
     * Returns true if the token signature is valid and not expired.
     */
    public boolean isTokenValid(String token) {
        try {
            getClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            log.debug("Invalid JWT token: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Extracts the email claim from the token.
     */
    public String extractEmail(String token) {
        return getClaims(token).get("email", String.class);
    }

    /**
     * Extracts the user id (subject) from the token.
     */
    public Long extractUserId(String token) {
        String subject = getClaims(token).getSubject();
        return Long.parseLong(subject);
    }

    /**
     * Extracts the roles claim from the token.
     */
    @SuppressWarnings("unchecked")
    public List<String> extractRoles(String token) {
        return getClaims(token).get("roles", List.class);
    }

    /**
     * Returns how many seconds until the access token expires.
     */
    public long getAccessTokenExpirationSeconds() {
        return jwtProperties.getAccessTokenExpirationMinutes() * 60L;
    }

    // ====================================================================
    // Private helpers
    // ====================================================================

    private Claims getClaims(String token) {
        return Jwts.parserBuilder()
            .setSigningKey(getSigningKey())
            .build()
            .parseClaimsJws(token)
            .getBody();
    }

    /**
     * Converts the secret string from application.yml into a cryptographic Key object.
     */
    private Key getSigningKey() {
        byte[] keyBytes = jwtProperties.getSecret().getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
