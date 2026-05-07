package com.bloodflow.auth.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

/**
 * Utility methods to get the currently logged-in user from the SecurityContext.
 *
 * Spring Security stores the authenticated user in a thread-local SecurityContext.
 * We use these helpers to retrieve that user in controllers and services.
 */
public final class SecurityUtils {

    private SecurityUtils() {
        // Utility class — not instantiable
    }

    /**
     * Returns the currently authenticated UserPrincipal, or empty if not authenticated.
     */
    public static Optional<UserPrincipal> getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserPrincipal principal) {
            return Optional.of(principal);
        }
        return Optional.empty();
    }

    /**
     * Returns the email of the currently authenticated user.
     */
    public static Optional<String> getCurrentUserEmail() {
        return getCurrentUser().map(UserPrincipal::getEmail);
    }

    /**
     * Returns the ID of the currently authenticated user.
     */
    public static Optional<Long> getCurrentUserId() {
        return getCurrentUser().map(UserPrincipal::getId);
    }
}
