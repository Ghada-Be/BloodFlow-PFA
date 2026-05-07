package com.bloodflow.auth.enums;

/**
 * Possible states for a user account.
 *
 * ACTIVE                    — account is usable, user can log in
 * DISABLED                  — admin blocked this account
 * PENDING_EMAIL_VERIFICATION — user registered but not yet verified email
 * LOCKED                    — too many failed login attempts
 */
public enum UserStatus {
    ACTIVE,
    DISABLED,
    PENDING_EMAIL_VERIFICATION,
    LOCKED
}
