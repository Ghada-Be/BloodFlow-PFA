package com.bloodflow.auth.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * Used by admin to create staff accounts (DOCTOR, STAFF, LAB_TECHNICIAN, etc.)
 */
@Data
public class CreateStaffRequest {

    @NotBlank(message = "First name is required")
    private String firstName;

    @NotBlank(message = "Last name is required")
    private String lastName;

    @NotBlank(message = "Email is required")
    @Email(message = "Email format is invalid")
    private String email;

    private String phoneNumber;

    @NotBlank(message = "Role is required")
    private String role;

    private String city;

    /** If empty, a temporary password is generated automatically */
    private String temporaryPassword;
}
