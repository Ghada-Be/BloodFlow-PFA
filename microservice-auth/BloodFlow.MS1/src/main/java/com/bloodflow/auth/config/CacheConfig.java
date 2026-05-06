package com.bloodflow.auth.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

/**
 * Cache configuration using Caffeine (an in-memory cache library).
 *
 * SIMPLE EXPLANATION:
 * Instead of asking MySQL "what roles exist?" every time,
 * we store the answer in memory for 5 minutes.
 * This makes responses faster.
 *
 * Cache names used in this project:
 *   - roles          : list of all roles (rarely changes)
 *   - userProfiles   : user profile data
 *   - tokenValidation: JWT validation results for MS2/MS3
 */
@Configuration
public class CacheConfig {

    @Bean
    public CacheManager cacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager("roles", "userProfiles", "tokenValidation");
        cacheManager.setCaffeine(
            Caffeine.newBuilder()
                .maximumSize(500)                // keep at most 500 entries
                .expireAfterWrite(5, TimeUnit.MINUTES) // discard after 5 minutes
        );
        return cacheManager;
    }
}
