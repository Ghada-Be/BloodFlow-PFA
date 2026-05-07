package com.bloodflow.auth.service;

import com.bloodflow.auth.dto.request.AssignRoleRequest;
import com.bloodflow.auth.dto.request.CreateStaffRequest;
import com.bloodflow.auth.dto.request.UpdateUserRequest;
import com.bloodflow.auth.dto.response.TemporaryPasswordResponse;
import com.bloodflow.auth.dto.response.UserResponse;
import com.bloodflow.auth.entity.Role;
import com.bloodflow.auth.entity.User;
import com.bloodflow.auth.enums.RoleName;
import com.bloodflow.auth.enums.UserStatus;
import com.bloodflow.auth.exception.BadRequestException;
import com.bloodflow.auth.exception.ResourceNotFoundException;
import com.bloodflow.auth.mapper.UserMapper;
import com.bloodflow.auth.repository.RoleRepository;
import com.bloodflow.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * Admin-only user management operations.
 *
 * SOLID note (SRP): Only admin operations on users.
 * Self-profile updates are in UserService.
 * Authentication is in AuthService.
 */
@Service
@RequiredArgsConstructor
public class AdminUserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuditService auditService;
    private final UserMapper userMapper;
    private final RefreshTokenService refreshTokenService;

    /** Roles that ADMIN can create via createStaff() */
    private static final Set<RoleName> STAFF_ROLES = Set.of(
        RoleName.DOCTOR,
        RoleName.STAFF,
        RoleName.LAB_TECHNICIAN,
        RoleName.BIOLOGIST,
        RoleName.DELIVERY_AGENT,
        RoleName.PROMOTER
    );

    // ====================================================================
    // LIST AND GET
    // ====================================================================

    @Transactional(readOnly = true)
    public List<UserResponse> getAllUsers() {
        return userMapper.toResponseList(userRepository.findAll());
    }

    @Transactional(readOnly = true)
    public UserResponse getUserById(Long id) {
        User user = findUserOrThrow(id);
        return userMapper.toResponse(user);
    }

    // ====================================================================
    // UPDATE USER
    // ====================================================================

    @Transactional
    public UserResponse updateUser(Long id, UpdateUserRequest request, String adminEmail) {
        User user = findUserOrThrow(id);

        if (request.getFirstName() != null) user.setFirstName(request.getFirstName());
        if (request.getLastName() != null) user.setLastName(request.getLastName());
        if (request.getPhoneNumber() != null) user.setPhoneNumber(request.getPhoneNumber());
        if (request.getCity() != null) user.setCity(request.getCity());

        User saved = userRepository.save(user);
        return userMapper.toResponse(saved);
    }

    // ====================================================================
    // CREATE STAFF
    // ====================================================================

    @Transactional
    public TemporaryPasswordResponse createStaff(CreateStaffRequest request, String adminEmail) {
        // Validate role
        RoleName roleName;
        try {
            roleName = RoleName.valueOf(request.getRole().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Rôle invalide: " + request.getRole());
        }

        if (!STAFF_ROLES.contains(roleName)) {
            throw new BadRequestException(
                "Ce rôle ne peut pas être créé par admin via ce endpoint. " +
                "Rôles autorisés: DOCTOR, STAFF, LAB_TECHNICIAN, BIOLOGIST, DELIVERY_AGENT, PROMOTER"
            );
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Un compte avec cet email existe déjà.");
        }

        // Generate or use provided temporary password
        String temporaryPassword = (request.getTemporaryPassword() != null && !request.getTemporaryPassword().isBlank())
            ? request.getTemporaryPassword()
            : generateTemporaryPassword();

        Role role = roleRepository.findByName(roleName)
            .orElseThrow(() -> new ResourceNotFoundException("Rôle non trouvé: " + roleName));

        User user = User.builder()
            .firstName(request.getFirstName())
            .lastName(request.getLastName())
            .email(request.getEmail())
            .phoneNumber(request.getPhoneNumber())
            .passwordHash(passwordEncoder.encode(temporaryPassword))
            .city(request.getCity())
            .emailVerified(true)
            .status(UserStatus.ACTIVE)
            .mustChangePassword(true) // Staff must change password on first login
            .roles(Set.of(role))
            .build();

        User saved = userRepository.save(user);

        auditService.logSuccess(adminEmail, "USER_CREATED_BY_ADMIN",
            "Admin created staff account: " + saved.getEmail() + " with role " + roleName);

        return TemporaryPasswordResponse.builder()
            .id(saved.getId())
            .email(saved.getEmail())
            .role(roleName.name())
            .temporaryPassword(temporaryPassword)
            .mustChangePassword(true)
            .build();
    }

    // ====================================================================
    // ROLES MANAGEMENT
    // ====================================================================

    @Transactional
    public UserResponse assignRole(Long userId, AssignRoleRequest request, String adminEmail) {
        User user = findUserOrThrow(userId);

        RoleName roleName;
        try {
            roleName = RoleName.valueOf(request.getRoleName().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Rôle invalide: " + request.getRoleName());
        }

        Role role = roleRepository.findByName(roleName)
            .orElseThrow(() -> new ResourceNotFoundException("Rôle non trouvé: " + roleName));

        user.getRoles().add(role);
        User saved = userRepository.save(user);

        auditService.logSuccess(adminEmail, "ROLE_ASSIGNED",
            "Role " + roleName + " assigned to " + user.getEmail());

        return userMapper.toResponse(saved);
    }

    @Transactional
    public UserResponse removeRole(Long userId, String roleName, String adminEmail) {
        User user = findUserOrThrow(userId);

        RoleName roleNameEnum;
        try {
            roleNameEnum = RoleName.valueOf(roleName.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Rôle invalide: " + roleName);
        }

        Role role = roleRepository.findByName(roleNameEnum)
            .orElseThrow(() -> new ResourceNotFoundException("Rôle non trouvé: " + roleName));

        if (!user.getRoles().contains(role)) {
            throw new BadRequestException("Ce rôle n'est pas attribué à cet utilisateur.");
        }

        if (user.getRoles().size() <= 1) {
            throw new BadRequestException("Impossible de retirer le dernier rôle d'un utilisateur.");
        }

        user.getRoles().remove(role);
        User saved = userRepository.save(user);

        auditService.logSuccess(adminEmail, "ROLE_REVOKED",
            "Role " + roleName + " removed from " + user.getEmail());

        return userMapper.toResponse(saved);
    }

    // ====================================================================
    // ENABLE / DISABLE
    // ====================================================================

    @Transactional
    public UserResponse enableUser(Long id, String adminEmail) {
        User user = findUserOrThrow(id);
        user.setStatus(UserStatus.ACTIVE);
        user.setFailedLoginAttempts(0);
        user.setLockoutUntil(null);
        User saved = userRepository.save(user);

        auditService.logSuccess(adminEmail, "USER_ENABLED",
            "User " + user.getEmail() + " enabled by admin");

        return userMapper.toResponse(saved);
    }

    @Transactional
    public UserResponse disableUser(Long id, String adminEmail) {
        User user = findUserOrThrow(id);
        user.setStatus(UserStatus.DISABLED);
        user.setFailedLoginAttempts(0);
        user.setLockoutUntil(null);
        User saved = userRepository.save(user);

        // Important for security: disable the account AND revoke every refresh token.
        // Existing stateless JWT access tokens are also blocked by CustomUserDetailsService/JwtAuthenticationFilter,
        // because disabled accounts are no longer loaded as authenticated users.
        refreshTokenService.revokeAllTokensForUser(user);

        auditService.logSuccess(adminEmail, "USER_DISABLED",
            "User " + user.getEmail() + " disabled by admin and refresh tokens revoked");

        return userMapper.toResponse(saved);
    }

    // ====================================================================
    // RESET TEMPORARY PASSWORD
    // ====================================================================

    @Transactional
    public TemporaryPasswordResponse resetTemporaryPassword(Long id, String adminEmail) {
        User user = findUserOrThrow(id);

        String temporaryPassword = generateTemporaryPassword();
        user.setPasswordHash(passwordEncoder.encode(temporaryPassword));
        user.setMustChangePassword(true);
        user.setFailedLoginAttempts(0);
        user.setLockoutUntil(null);
        userRepository.save(user);
        refreshTokenService.revokeAllTokensForUser(user);

        auditService.logSuccess(adminEmail, "PASSWORD_CHANGED",
            "Admin reset temporary password for: " + user.getEmail());

        return TemporaryPasswordResponse.builder()
            .id(user.getId())
            .email(user.getEmail())
            .temporaryPassword(temporaryPassword)
            .mustChangePassword(true)
            .build();
    }

    // ====================================================================
    // Private helpers
    // ====================================================================

    private User findUserOrThrow(Long id) {
        return userRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé avec l'id: " + id));
    }

    /**
     * Generates a random temporary password.
     * Example: Bf-A3x9P (easy to remember, secure enough for demo).
     */
    private String generateTemporaryPassword() {
        return "Tmp-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}
