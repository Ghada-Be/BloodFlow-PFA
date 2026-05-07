package com.bloodflow.auth.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthResponse {
    private String accessToken;
    private String token;
    private String refreshToken;
    private long expiresIn;
    private UserResponse user;

    /**
     * Frontend helper fields.
     * If mustChangePassword = true, React should redirect the user to /change-password
     * before allowing access to the dashboard.
     */
    private boolean mustChangePassword;
    private String nextAction;
    private String redirectTo;
}
