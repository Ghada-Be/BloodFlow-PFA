package com.bloodflow.auth.controller;

import com.bloodflow.auth.dto.response.ApiResponse;
import com.bloodflow.auth.dto.response.RoleResponse;
import com.bloodflow.auth.service.RoleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Read-only role endpoints.
 * Public — any client can list roles (useful for the registration form dropdown).
 */
@RestController
@RequestMapping("/api/roles")
@RequiredArgsConstructor
@Tag(name = "Roles", description = "List available roles")
public class RoleController {

    private final RoleService roleService;

    @GetMapping
    @Operation(summary = "Get all roles")
    public ResponseEntity<ApiResponse<List<RoleResponse>>> getAllRoles() {
        List<RoleResponse> result = roleService.getAllRoles();
        return ResponseEntity.ok(ApiResponse.success("Liste des rôles.", result));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get role by ID")
    public ResponseEntity<ApiResponse<RoleResponse>> getRoleById(@PathVariable Long id) {
        RoleResponse result = roleService.getRoleById(id);
        return ResponseEntity.ok(ApiResponse.success("Rôle trouvé.", result));
    }
}
