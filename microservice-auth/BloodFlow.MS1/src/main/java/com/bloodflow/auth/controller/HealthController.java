package com.bloodflow.auth.controller;

import com.bloodflow.auth.dto.response.ApiResponse;
import com.bloodflow.auth.dto.response.HealthResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Simple health check endpoint.
 * MS2 and MS3 can call this to verify that MS1 is running.
 */
@RestController
@RequestMapping("/api/health")
@Tag(name = "Health", description = "Service health check")
public class HealthController {

    @GetMapping
    @Operation(summary = "Check if the service is running")
    public ResponseEntity<ApiResponse<HealthResponse>> health() {
        HealthResponse data = HealthResponse.builder()
            .status("UP")
            .service("BloodFlow Auth Service")
            .database("MySQL")
            .timestamp(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
            .build();
        return ResponseEntity.ok(ApiResponse.success("Service opérationnel", data));
    }
}
