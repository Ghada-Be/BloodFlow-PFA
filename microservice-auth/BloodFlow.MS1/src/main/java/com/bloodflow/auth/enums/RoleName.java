package com.bloodflow.auth.enums;

/**
 * All roles in the BloodFlow platform.
 *
 * PUBLIC roles (users can register themselves):
 *   DONOR, PATIENT
 *
 * STAFF roles (only admin can create these accounts):
 *   DOCTOR, STAFF, LAB_TECHNICIAN, BIOLOGIST, DELIVERY_AGENT, PROMOTER
 *
 * ADMIN role: created automatically at startup.
 */
public enum RoleName {
    ADMIN,
    DONOR,
    PATIENT,
    DOCTOR,
    STAFF,
    LAB_TECHNICIAN,
    BIOLOGIST,
    DELIVERY_AGENT,
    PROMOTER
}
