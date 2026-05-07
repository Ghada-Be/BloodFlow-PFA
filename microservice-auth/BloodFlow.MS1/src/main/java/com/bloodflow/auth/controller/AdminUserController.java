package com.bloodflow.auth.controller;

import com.bloodflow.auth.dto.request.AssignRoleRequest;
import com.bloodflow.auth.dto.request.CreateStaffRequest;
import com.bloodflow.auth.dto.request.UpdateUserRequest;
import com.bloodflow.auth.dto.response.ApiResponse;
import com.bloodflow.auth.dto.response.TemporaryPasswordResponse;
import com.bloodflow.auth.dto.response.UserResponse;
import com.bloodflow.auth.security.UserPrincipal;
import com.bloodflow.auth.service.AdminUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Admin-only user management endpoints.
 * All endpoints require ADMIN role.
 */
@RestController
@RequestMapping("/api/admin/users")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Admin - User Management", description = "Admin operations on users")
@SecurityRequirement(name = "Bearer Authentication")
public class AdminUserController {

    private final AdminUserService adminUserService;

    @GetMapping
    @Operation(summary = "Get all users")
    public ResponseEntity<ApiResponse<List<UserResponse>>> getAllUsers() {
        List<UserResponse> result = adminUserService.getAllUsers();
        return ResponseEntity.ok(ApiResponse.success("Liste des utilisateurs.", result));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get user by ID")
    public ResponseEntity<ApiResponse<UserResponse>> getUserById(@PathVariable Long id) {
        UserResponse result = adminUserService.getUserById(id);
        return ResponseEntity.ok(ApiResponse.success("Utilisateur trouvé.", result));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update user info")
    public ResponseEntity<ApiResponse<UserResponse>> updateUser(
            @PathVariable Long id,
            @RequestBody UpdateUserRequest request,
            @AuthenticationPrincipal UserPrincipal principal) {

        UserResponse result = adminUserService.updateUser(id, request, principal.getEmail());
        return ResponseEntity.ok(ApiResponse.success("Utilisateur mis à jour.", result));
    }

    @PostMapping("/staff")
    @Operation(summary = "Create a staff account (DOCTOR, STAFF, LAB_TECHNICIAN, etc.)")
    public ResponseEntity<ApiResponse<TemporaryPasswordResponse>> createStaff(
            @Valid @RequestBody CreateStaffRequest request,
            @AuthenticationPrincipal UserPrincipal principal) {

        TemporaryPasswordResponse result = adminUserService.createStaff(request, principal.getEmail());
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.success("Compte staff créé avec succès.", result));
    }

    @PostMapping("/{id}/roles")
    @Operation(summary = "Assign a role to a user")
    public ResponseEntity<ApiResponse<UserResponse>> assignRole(
            @PathVariable Long id,
            @Valid @RequestBody AssignRoleRequest request,
            @AuthenticationPrincipal UserPrincipal principal) {

        UserResponse result = adminUserService.assignRole(id, request, principal.getEmail());
        return ResponseEntity.ok(ApiResponse.success("Rôle assigné.", result));
    }

    @DeleteMapping("/{id}/roles/{roleName}")
    @Operation(summary = "Remove a role from a user")
    public ResponseEntity<ApiResponse<UserResponse>> removeRole(
            @PathVariable Long id,
            @PathVariable String roleName,
            @AuthenticationPrincipal UserPrincipal principal) {

        UserResponse result = adminUserService.removeRole(id, roleName, principal.getEmail());
        return ResponseEntity.ok(ApiResponse.success("Rôle retiré.", result));
    }

    @PatchMapping("/{id}/enable")
    @Operation(summary = "Enable a user account")
    public ResponseEntity<ApiResponse<UserResponse>> enableUser(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal principal) {

        UserResponse result = adminUserService.enableUser(id, principal.getEmail());
        return ResponseEntity.ok(ApiResponse.success("Compte activé.", result));
    }

    @PatchMapping("/{id}/disable")
    @Operation(summary = "Disable a user account")
    public ResponseEntity<ApiResponse<UserResponse>> disableUser(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal principal) {

        UserResponse result = adminUserService.disableUser(id, principal.getEmail());
        return ResponseEntity.ok(ApiResponse.success("Compte désactivé.", result));
    }

    @PostMapping("/{id}/reset-temporary-password")
    @Operation(summary = "Reset user password to a new temporary password")
    public ResponseEntity<ApiResponse<TemporaryPasswordResponse>> resetTemporaryPassword(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal principal) {

        TemporaryPasswordResponse result = adminUserService.resetTemporaryPassword(id, principal.getEmail());
        return ResponseEntity.ok(ApiResponse.success("Mot de passe temporaire réinitialisé.", result));
    }
}
