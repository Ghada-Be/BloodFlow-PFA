package com.bloodflow.auth.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TokenValidationResponse {
    private boolean valid;
    private Long userId;
    private String email;
    private String fullName;
    private List<String> roles;
    private String status;
    private Boolean emailVerified;
}
