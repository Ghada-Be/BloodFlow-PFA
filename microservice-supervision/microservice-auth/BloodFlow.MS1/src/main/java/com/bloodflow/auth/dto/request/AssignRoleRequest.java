package com.bloodflow.auth.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AssignRoleRequest {

    @NotBlank(message = "Role name is required")
    private String roleName;
}
