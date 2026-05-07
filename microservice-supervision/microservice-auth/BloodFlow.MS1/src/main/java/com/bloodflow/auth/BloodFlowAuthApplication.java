package com.bloodflow.auth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.cache.annotation.EnableCaching;

/**
 * BloodFlow Auth Service — Microservice 1
 *
 * Entry point. Spring Boot scans all beans automatically.
 * @ConfigurationPropertiesScan registers JwtProperties from application.yml.
 */
@SpringBootApplication
@EnableCaching
@ConfigurationPropertiesScan
public class BloodFlowAuthApplication {

    public static void main(String[] args) {
        SpringApplication.run(BloodFlowAuthApplication.class, args);
    }
}
