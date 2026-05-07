package com.bloodflow.auth.service;

import com.bloodflow.auth.entity.AuditLog;
import com.bloodflow.auth.repository.AuditLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Records security events in the audit_logs table.
 *
 * AUDIT LOG ACTIONS:
 *   REGISTER_SUCCESS, LOGIN_SUCCESS, LOGIN_FAILED, LOGOUT,
 *   PASSWORD_CHANGED, PASSWORD_RESET_REQUESTED, PASSWORD_RESET_SUCCESS,
 *   EMAIL_VERIFIED, USER_CREATED_BY_ADMIN, USER_DISABLED, USER_ENABLED,
 *   ROLE_ASSIGNED, ROLE_REVOKED
 *
 * IMPORTANT RULES:
 * - Never store passwords or JWT tokens in audit logs.
 * - If audit logging fails, log a warning but do NOT crash the app.
 *
 * SOLID note (SRP): This class only handles audit logging.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuditService {

    private final AuditLogRepository auditLogRepository;

    /**
     * Logs a successful action.
     */
    public void logSuccess(String userEmail, String action, String details) {
        save(userEmail, action, details, true);
    }

    /**
     * Logs a failed action.
     */
    public void logFailure(String userEmail, String action, String details) {
        save(userEmail, action, details, false);
    }

    // ====================================================================
    // Private helper
    // ====================================================================

    private void save(String userEmail, String action, String details, boolean success) {
        try {
            AuditLog log = AuditLog.builder()
                .userEmail(userEmail != null ? userEmail : "unknown")
                .action(action)
                .details(details)
                .success(success)
                .build();
            auditLogRepository.save(log);
        } catch (Exception e) {
            // Audit failure must never break the main flow
            log.warn("Failed to save audit log for action '{}' and user '{}': {}", action, userEmail, e.getMessage());
        }
    }
}
