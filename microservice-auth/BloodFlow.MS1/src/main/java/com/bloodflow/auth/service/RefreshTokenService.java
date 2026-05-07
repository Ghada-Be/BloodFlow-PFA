package com.bloodflow.auth.service;

import com.bloodflow.auth.config.JwtProperties;
import com.bloodflow.auth.entity.RefreshToken;
import com.bloodflow.auth.entity.User;
import com.bloodflow.auth.exception.BadRequestException;
import com.bloodflow.auth.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Manages refresh tokens.
 *
 * A refresh token is a long-lived token stored in MySQL.
 * When the short-lived access token (JWT) expires,
 * the frontend sends the refresh token to get a new access token.
 *
 * SOLID note (SRP): Only handles refresh token logic.
 */
@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtProperties jwtProperties;

    /**
     * Creates and saves a new refresh token for the given user.
     */
    @Transactional
    public RefreshToken createRefreshToken(User user) {
        RefreshToken refreshToken = RefreshToken.builder()
            .token(UUID.randomUUID().toString())
            .user(user)
            .expiresAt(LocalDateTime.now().plusDays(jwtProperties.getRefreshTokenExpirationDays()))
            .revoked(false)
            .build();
        return refreshTokenRepository.save(refreshToken);
    }

    /**
     * Validates the given refresh token string.
     * Throws BadRequestException if invalid, expired or revoked.
     */
    @Transactional(readOnly = true)
    public RefreshToken validateRefreshToken(String tokenValue) {
        RefreshToken token = refreshTokenRepository.findByToken(tokenValue)
            .orElseThrow(() -> new BadRequestException("Refresh token invalide."));

        if (token.isExpiredOrRevoked()) {
            throw new BadRequestException("Refresh token expiré ou révoqué.");
        }

        return token;
    }

    /**
     * Revokes a single refresh token (used on logout).
     */
    @Transactional
    public void revokeToken(String tokenValue) {
        refreshTokenRepository.findByToken(tokenValue).ifPresent(token -> {
            token.setRevoked(true);
            refreshTokenRepository.save(token);
        });
    }

    /**
     * Revokes ALL refresh tokens for a user (logout from all devices).
     */
    @Transactional
    public void revokeAllTokensForUser(User user) {
        refreshTokenRepository.revokeAllByUser(user);
    }
}
