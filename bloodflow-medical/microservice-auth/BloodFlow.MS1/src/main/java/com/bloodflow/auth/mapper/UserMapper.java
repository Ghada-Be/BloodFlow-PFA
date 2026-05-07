package com.bloodflow.auth.mapper;

import com.bloodflow.auth.dto.response.UserResponse;
import com.bloodflow.auth.entity.User;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Converts User entity to UserResponse DTO.
 *
 * WHY USE A MAPPER?
 * - Keeps entities separate from DTOs (what we show to the frontend).
 * - Ensures we never accidentally return passwordHash to the frontend.
 *
 * SOLID note (SRP): Only handles entity-to-DTO conversion.
 */
@Component
public class UserMapper {

    public UserResponse toResponse(User user) {
        List<String> roles = user.getRoles().stream()
            .map(role -> role.getName().name())
            .collect(Collectors.toList());

        return UserResponse.builder()
            .id(user.getId())
            .firstName(user.getFirstName())
            .lastName(user.getLastName())
            .fullName(user.getFullName())
            .email(user.getEmail())
            .phoneNumber(user.getPhoneNumber())
            .city(user.getCity())
            .emailVerified(user.isEmailVerified())
            .mustChangePassword(user.isMustChangePassword())
            .status(user.getStatus().name())
            .roles(roles)
            .lastLoginAt(user.getLastLoginAt())
            .createdAt(user.getCreatedAt())
            .build();
    }

    public List<UserResponse> toResponseList(List<User> users) {
        return users.stream()
            .map(this::toResponse)
            .collect(Collectors.toList());
    }
}
