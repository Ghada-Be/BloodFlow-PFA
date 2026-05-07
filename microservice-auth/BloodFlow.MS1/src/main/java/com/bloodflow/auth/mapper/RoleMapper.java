package com.bloodflow.auth.mapper;

import com.bloodflow.auth.dto.response.RoleResponse;
import com.bloodflow.auth.entity.Role;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Converts Role entity to RoleResponse DTO.
 */
@Component
public class RoleMapper {

    public RoleResponse toResponse(Role role) {
        return RoleResponse.builder()
            .id(role.getId())
            .name(role.getName().name())
            .description(role.getDescription())
            .createdAt(role.getCreatedAt())
            .build();
    }

    public List<RoleResponse> toResponseList(List<Role> roles) {
        return roles.stream()
            .map(this::toResponse)
            .collect(Collectors.toList());
    }
}
