package com.bloodflow.auth.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * Records important security events.
 *
 * Examples: LOGIN_SUCCESS, LOGIN_FAILED, USER_DISABLED, ROLE_ASSIGNED
 *
 * IMPORTANT: Never store passwords or tokens in audit logs.
 */
@Entity
@Table(name = "audit_logs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Email of the user who performed the action (or target user) */
    @Column(nullable = false)
    private String userEmail;

    /** Action name, e.g. LOGIN_SUCCESS, USER_DISABLED */
    @Column(nullable = false)
    private String action;

    /** Extra details, e.g. "Role DOCTOR assigned to sara@bloodflow.ma" */
    @Column(length = 1000)
    private String details;

    /** Was the action successful? */
    @Column(nullable = false)
    @Builder.Default
    private boolean success = true;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
}
