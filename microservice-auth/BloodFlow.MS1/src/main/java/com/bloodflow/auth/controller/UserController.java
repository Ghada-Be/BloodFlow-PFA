package com.bloodflow.auth.controller;

import com.bloodflow.auth.dto.request.UpdateProfileRequest;
import com.bloodflow.auth.dto.response.ApiResponse;
import com.bloodflow.auth.dto.response.UserResponse;
import com.bloodflow.auth.security.UserPrincipal;
import com.bloodflow.auth.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

/**
 * User profile endpoints (for the logged-in user).
 * All endpoints require authentication.
 */
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "User Profile", description = "View and update own profile")
@SecurityRequirement(name = "Bearer Authentication")
public class UserController {

    private final UserService userService;

    @GetMapping("/profile")
    @Operation(summary = "Get current user profile")
    public ResponseEntity<ApiResponse<UserResponse>> getProfile(
            @AuthenticationPrincipal UserPrincipal principal) {

        UserResponse result = userService.getProfile(principal);
        return ResponseEntity.ok(ApiResponse.success("Profil récupéré.", result));
    }

    @PutMapping("/profile")
    @Operation(summary = "Update current user profile")
    public ResponseEntity<ApiResponse<UserResponse>> updateProfile(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestBody UpdateProfileRequest request) {

        UserResponse result = userService.updateProfile(principal, request);
        return ResponseEntity.ok(ApiResponse.success("Profil mis à jour.", result));
    }
}
