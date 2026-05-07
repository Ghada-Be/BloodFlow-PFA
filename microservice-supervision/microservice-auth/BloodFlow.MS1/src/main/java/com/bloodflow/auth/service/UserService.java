package com.bloodflow.auth.service;

import com.bloodflow.auth.dto.request.UpdateProfileRequest;
import com.bloodflow.auth.dto.response.UserResponse;
import com.bloodflow.auth.entity.User;
import com.bloodflow.auth.exception.ResourceNotFoundException;
import com.bloodflow.auth.mapper.UserMapper;
import com.bloodflow.auth.repository.UserRepository;
import com.bloodflow.auth.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Handles the logged-in user's own profile.
 *
 * SOLID note (SRP): Only handles the current user's profile.
 * Admin user management is in AdminUserService.
 */
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    /**
     * Returns the current user's profile.
     * Cached by userId for 5 minutes.
     */
    @Cacheable(value = "userProfiles", key = "#principal.id")
    @Transactional(readOnly = true)
    public UserResponse getProfile(UserPrincipal principal) {
        User user = userRepository.findById(principal.getId())
            .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé."));
        return userMapper.toResponse(user);
    }

    /**
     * Updates the current user's personal info.
     * Clears the cached profile so fresh data is returned next time.
     */
    @CacheEvict(value = "userProfiles", key = "#principal.id")
    @Transactional
    public UserResponse updateProfile(UserPrincipal principal, UpdateProfileRequest request) {
        User user = userRepository.findById(principal.getId())
            .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé."));

        if (request.getFirstName() != null && !request.getFirstName().isBlank()) {
            user.setFirstName(request.getFirstName());
        }
        if (request.getLastName() != null && !request.getLastName().isBlank()) {
            user.setLastName(request.getLastName());
        }
        if (request.getPhoneNumber() != null) {
            user.setPhoneNumber(request.getPhoneNumber());
        }
        if (request.getCity() != null) {
            user.setCity(request.getCity());
        }

        User saved = userRepository.save(user);
        return userMapper.toResponse(saved);
    }
}
