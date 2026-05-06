package com.bloodflow.auth.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class TokenValidationRequest {

    @NotBlank(message = "Token is required")
    private String token;
}
