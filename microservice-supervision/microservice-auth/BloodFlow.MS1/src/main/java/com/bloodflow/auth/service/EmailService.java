package com.bloodflow.auth.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Email service — DEMO MODE only.
 *
 * In demo mode (email-verification-enabled=false),
 * we do NOT send real emails. Instead, we print the links to the console.
 *
 * This is perfect for development and student projects.
 * In production, you would replace these methods with a real email sender
 * (e.g., Spring Mail with SendGrid or Mailtrap).
 *
 * SOLID note (OCP): This class is easy to replace with a real email sender.
 */
@Slf4j
@Service
public class EmailService {

    @Value("${bloodflow.frontend.base-url}")
    private String frontendBaseUrl;

    /**
     * Simulates sending an email verification link.
     * Prints the link to the console.
     */
    public void sendVerificationEmail(String email, String token) {
        String link = frontendBaseUrl + "/verify-email?token=" + token;
        log.info("==========================================================");
        log.info("[DEMO EMAIL] Email Verification for: {}", email);
        log.info("[DEMO EMAIL] Verification link: {}", link);
        log.info("==========================================================");
    }

    /**
     * Simulates sending a password reset email.
     * Prints the link to the console.
     */
    public void sendPasswordResetEmail(String email, String token) {
        String link = frontendBaseUrl + "/reset-password?token=" + token;
        log.info("==========================================================");
        log.info("[DEMO EMAIL] Password Reset for: {}", email);
        log.info("[DEMO EMAIL] Reset link: {}", link);
        log.info("==========================================================");
    }
}
