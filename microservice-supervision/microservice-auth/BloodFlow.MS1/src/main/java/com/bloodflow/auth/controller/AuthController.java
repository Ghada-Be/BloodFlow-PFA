package com.bloodflow.auth.controller;

import com.bloodflow.auth.dto.request.*;
import com.bloodflow.auth.dto.response.ApiResponse;
import com.bloodflow.auth.dto.response.AuthResponse;
import com.bloodflow.auth.dto.response.UserResponse;
import com.bloodflow.auth.security.UserPrincipal;
import com.bloodflow.auth.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

/**
 * Authentication endpoints.
 *
 * Public:  /api/auth/register, /api/auth/login, /api/auth/refresh,
 *          /api/auth/forgot-password, /api/auth/reset-password,
 *          /api/auth/verify-email, /api/auth/resend-verification-email
 *
 * Protected: /api/auth/me, /api/auth/logout, /api/auth/logout-all,
 *            /api/auth/change-password
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Register, login, logout, password management")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    @Operation(summary = "Register a new DONOR or PATIENT account")
    public ResponseEntity<ApiResponse<UserResponse>> register(
            @Valid @RequestBody RegisterRequest request) {

        UserResponse result = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.success("Compte créé avec succès. Vous pouvez vous connecter.", result));
    }

    @PostMapping("/login")
    @Operation(summary = "Login with email and password")
    public ResponseEntity<ApiResponse<AuthResponse>> login(
            @Valid @RequestBody LoginRequest request) {

        AuthResponse result = authService.login(request);
        return ResponseEntity.ok(ApiResponse.success("Connexion réussie", result));
    }

    @PostMapping("/refresh")
    @Operation(summary = "Get a new access token using a refresh token")
    public ResponseEntity<ApiResponse<AuthResponse>> refresh(
            @Valid @RequestBody RefreshTokenRequest request) {

        AuthResponse result = authService.refreshToken(request);
        return ResponseEntity.ok(ApiResponse.success("Token renouvelé", result));
    }

    @PostMapping("/logout")
    @Operation(summary = "Logout (revoke refresh token)", security = @SecurityRequirement(name = "Bearer Authentication"))
    public ResponseEntity<ApiResponse<Void>> logout(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestBody(required = false) LogoutRequest request) {

        authService.logout(principal.getEmail(), request != null ? request : new LogoutRequest());
        return ResponseEntity.ok(ApiResponse.success("Déconnexion réussie."));
    }

    @PostMapping("/logout-all")
    @Operation(summary = "Logout from all devices", security = @SecurityRequirement(name = "Bearer Authentication"))
    public ResponseEntity<ApiResponse<Void>> logoutAll(
            @AuthenticationPrincipal UserPrincipal principal) {

        authService.logoutAll(principal);
        return ResponseEntity.ok(ApiResponse.success("Déconnexion de tous les appareils réussie."));
    }

    @GetMapping("/me")
    @Operation(summary = "Get current authenticated user", security = @SecurityRequirement(name = "Bearer Authentication"))
    public ResponseEntity<ApiResponse<UserResponse>> me(
            @AuthenticationPrincipal UserPrincipal principal) {

        UserResponse result = authService.getCurrentUser(principal);
        return ResponseEntity.ok(ApiResponse.success("Utilisateur récupéré.", result));
    }

    @PostMapping("/change-password")
    @Operation(summary = "Change own password", security = @SecurityRequirement(name = "Bearer Authentication"))
    public ResponseEntity<ApiResponse<Void>> changePassword(
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody ChangePasswordRequest request) {

        authService.changePassword(principal, request);
        return ResponseEntity.ok(ApiResponse.success("Mot de passe modifié avec succès."));
    }

    @PostMapping("/forgot-password")
    @Operation(summary = "Request a password reset email")
    public ResponseEntity<ApiResponse<Void>> forgotPassword(
            @Valid @RequestBody ForgotPasswordRequest request) {

        authService.forgotPassword(request);
        // Always the same response for security
        return ResponseEntity.ok(ApiResponse.success(
            "Si cet email existe, un lien de réinitialisation a été envoyé."));
    }

    @PostMapping("/reset-password")
    @Operation(summary = "Reset password using token from email")
    public ResponseEntity<ApiResponse<Void>> resetPassword(
            @Valid @RequestBody ResetPasswordRequest request) {

        authService.resetPassword(request);
        return ResponseEntity.ok(ApiResponse.success("Mot de passe réinitialisé avec succès."));
    }

    @PostMapping("/verify-email")
    @Operation(summary = "Verify email using token from verification email")
    public ResponseEntity<ApiResponse<Void>> verifyEmail(
            @Valid @RequestBody VerifyEmailRequest request) {

        authService.verifyEmail(request);
        return ResponseEntity.ok(ApiResponse.success("Email vérifié avec succès. Vous pouvez vous connecter."));
    }

    @PostMapping("/resend-verification-email")
    @Operation(summary = "Resend email verification link")
    public ResponseEntity<ApiResponse<Void>> resendVerificationEmail(
            @Valid @RequestBody ResendVerificationEmailRequest request) {

        authService.resendVerificationEmail(request);
        return ResponseEntity.ok(ApiResponse.success(
            "Si cet email existe et n'est pas vérifié, un nouveau lien a été envoyé."));
    }
}
