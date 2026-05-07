package com.bloodflow.auth.service;

import com.bloodflow.auth.dto.response.RoleResponse;
import com.bloodflow.auth.entity.Role;
import com.bloodflow.auth.enums.RoleName;
import com.bloodflow.auth.exception.ResourceNotFoundException;
import com.bloodflow.auth.mapper.RoleMapper;
import com.bloodflow.auth.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service for reading roles.
 *
 * Roles are seeded at startup and rarely change.
 * We cache the full list to avoid repeated database reads.
 */
@Service
@RequiredArgsConstructor
public class RoleService {

    private final RoleRepository roleRepository;
    private final RoleMapper roleMapper;

    /**
     * Returns all roles.
     * The result is cached for 5 minutes (see CacheConfig).
     */
    @Cacheable("roles")
    @Transactional(readOnly = true)
    public List<RoleResponse> getAllRoles() {
        return roleMapper.toResponseList(roleRepository.findAll());
    }

    @Transactional(readOnly = true)
    public RoleResponse getRoleById(Long id) {
        Role role = roleRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Rôle non trouvé avec l'id: " + id));
        return roleMapper.toResponse(role);
    }

    /**
     * Finds a Role entity by its enum name.
     * Used internally by other services.
     */
    @Transactional(readOnly = true)
    public Role findRoleEntityByName(RoleName name) {
        return roleRepository.findByName(name)
            .orElseThrow(() -> new ResourceNotFoundException("Rôle non trouvé: " + name));
    }
}
