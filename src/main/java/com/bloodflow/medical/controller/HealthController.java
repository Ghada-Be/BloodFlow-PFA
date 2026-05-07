package com.bloodflow.medical.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
public class HealthController {

    @Value("${spring.application.name:bloodflow-medical-service}")
    private String serviceName;

    @GetMapping({"/api/health", "/api/medical/health"})
    public Map<String, Object> health() {
        return Map.of(
                "status", "UP",
                "service", serviceName,
                "database", "PostgreSQL",
                "port", 8082,
                "timestamp", LocalDateTime.now().toString()
        );
    }
}
