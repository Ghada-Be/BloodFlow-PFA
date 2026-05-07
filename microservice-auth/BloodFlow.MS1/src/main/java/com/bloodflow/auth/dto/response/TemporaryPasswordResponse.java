package com.bloodflow.auth.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TemporaryPasswordResponse {
    private Long id;
    private String email;
    private String role;
    private String temporaryPassword;
    private boolean mustChangePassword;
}
