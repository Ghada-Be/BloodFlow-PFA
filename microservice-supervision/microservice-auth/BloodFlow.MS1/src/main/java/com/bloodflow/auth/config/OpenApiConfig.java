package com.bloodflow.auth.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.context.annotation.Configuration;

/**
 * Configures Swagger UI at http://localhost:8081/swagger-ui.html
 *
 * You can test all endpoints from the browser without Postman.
 * Click "Authorize" and paste your JWT token to test protected endpoints.
 */
@Configuration
@OpenAPIDefinition(
    info = @Info(
        title = "BloodFlow Auth Service",
        version = "1.0",
        description = "Microservice 1 - Authentication, Users and Roles"
    )
)
@SecurityScheme(
    name = "Bearer Authentication",
    type = SecuritySchemeType.HTTP,
    bearerFormat = "JWT",
    scheme = "bearer"
)
public class OpenApiConfig {
}
