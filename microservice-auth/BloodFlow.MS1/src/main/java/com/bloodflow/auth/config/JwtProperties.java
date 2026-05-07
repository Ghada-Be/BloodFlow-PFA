package com.bloodflow.auth.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Maps jwt.* properties from application.yml into this class.
 *
 * Spring reads the YAML and fills the fields automatically.
 * We use @ConfigurationProperties so changes to the config
 * require no code changes (Open/Closed Principle).
 */
@Data
@Component
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {

    private String secret;
    private String issuer;
    private String audience;
    private long accessTokenExpirationMinutes;
    private long refreshTokenExpirationDays;
}
