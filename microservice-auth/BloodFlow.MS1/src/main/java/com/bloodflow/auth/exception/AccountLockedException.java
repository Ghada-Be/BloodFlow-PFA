package com.bloodflow.auth.exception;

/**
 * Thrown when a user account is temporarily locked after repeated failed login attempts.
 */
public class AccountLockedException extends RuntimeException {
    public AccountLockedException(String message) {
        super(message);
    }
}
