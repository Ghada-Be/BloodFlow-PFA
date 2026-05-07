package com.bloodflow.auth.controller;

import com.bloodflow.auth.dto.request.TokenValidationRequest;
import com.bloodflow.auth.dto.response.ApiResponse;
import com.bloodflow.auth.dto.response.TokenValidationResponse;
import com.bloodflow.auth.dto.response.UserResponse;
import com.bloodflow.auth.entity.User;
import com.bloodflow.auth.exception.ResourceNotFoundException;
import com.bloodflow.auth.mapper.UserMapper;
import com.bloodflow.auth.repository.UserRepository;
import com.bloodflow.auth.security.UserPrincipal;
import com.bloodflow.auth.service.JwtService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * REST API endpoints for communication with MS2 and MS3.
 *
 * MS2 (Medical) and MS3 (Notifications) can call these endpoints to:
 * 1. Check if the auth service is alive (/ping)
 * 2. Validate a JWT token (/validate-token)
 * 3. Check if a user exists (/users/{id}/exists)
 * 4. Get safe user info (/users/{id})
 * 5. Get current authenticated user (/current-user)
 *
 * IMPORTANT: /ping and /validate-token are public (no JWT required),
 * because MS2/MS3 need to call them before they have a user context.
 */
@RestController
@RequestMapping("/api/integration")
@RequiredArgsConstructor
@Tag(name = "Integration API", description = "REST API for MS2 and MS3 communication")
public class IntegrationController {

    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    /**
     * Simple ping endpoint — confirms the auth service is reachable.
     */
    @GetMapping("/ping")
    @Operation(summary = "Ping — check if auth service is reachable")
    public ResponseEntity<ApiResponse<Map<String, String>>> ping() {
        Map<String, String> data = Map.of(
            "service", "BloodFlow Auth Service",
            "status", "UP"
        );
        return ResponseEntity.ok(ApiResponse.success("Auth service reachable", data));
    }

    /**
     * Validates a JWT token.
     * MS2 and MS3 call this to verify that a user's token is valid
     * before processing their request.
     *
     * The result is cached for 5 minutes to reduce repeated database calls.
     */
    @PostMapping("/validate-token")
    @Operation(summary = "Validate a JWT token")
    @Cacheable(value = "tokenValidation", key = "#request.token")
    public ResponseEntity<ApiResponse<TokenValidationResponse>> validateToken(
            @Valid @RequestBody TokenValidationRequest request) {

        if (!jwtService.isTokenValid(request.getToken())) {
            TokenValidationResponse invalid = TokenValidationResponse.builder()
                .valid(false)
                .build();
            return ResponseEntity.ok(ApiResponse.error("Token invalide", invalid));
        }

        try {
            Long userId = jwtService.extractUserId(request.getToken());
            String email = jwtService.extractEmail(request.getToken());
            List<String> roles = jwtService.extractRoles(request.getToken());

            User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

            TokenValidationResponse response = TokenValidationResponse.builder()
                .valid(true)
                .userId(userId)
                .email(email)
                .fullName(user.getFullName())
                .roles(roles)
                .status(user.getStatus().name())
                .emailVerified(user.isEmailVerified())
                .build();

            return ResponseEntity.ok(ApiResponse.success("Token valide", response));

        } catch (Exception e) {
            TokenValidationResponse invalid = TokenValidationResponse.builder()
                .valid(false)
                .build();
            return ResponseEntity.ok(ApiResponse.error("Token invalide", invalid));
        }
    }

    /**
     * Returns whether a user with the given ID exists.
     * Useful for MS2 to verify a user before creating medical records.
     */
    @GetMapping("/users/{id}/exists")
    @Operation(summary = "Check if a user exists by ID")
    public ResponseEntity<ApiResponse<Boolean>> userExists(@PathVariable Long id) {
        boolean exists = userRepository.existsById(id);
        return ResponseEntity.ok(ApiResponse.success("Vérification utilisateur.", exists));
    }

    /**
     * Returns safe user info (no password, no sensitive data).
     * MS2 and MS3 can use this to get the user's name, city, role, etc.
     */
    @GetMapping("/users/{id}")
    @Operation(summary = "Get safe user info by ID")
    public ResponseEntity<ApiResponse<UserResponse>> getUserById(@PathVariable Long id) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé avec l'id: " + id));

        return ResponseEntity.ok(ApiResponse.success("Utilisateur trouvé.", userMapper.toResponse(user)));
    }

    /**
     * Returns the current authenticated user.
     * Requires a valid Bearer token.
     */
    @GetMapping("/current-user")
    @Operation(
        summary = "Get current authenticated user (requires Bearer token)",
        security = @SecurityRequirement(name = "Bearer Authentication")
    )
    public ResponseEntity<ApiResponse<UserResponse>> getCurrentUser(
            @AuthenticationPrincipal UserPrincipal principal) {

        User user = userRepository.findById(principal.getId())
            .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé."));

        return ResponseEntity.ok(ApiResponse.success("Utilisateur courant.", userMapper.toResponse(user)));
    }
}
