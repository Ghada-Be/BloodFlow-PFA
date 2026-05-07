package com.bloodflow.auth.service;

import com.bloodflow.auth.dto.request.*;
import com.bloodflow.auth.dto.response.AuthResponse;
import com.bloodflow.auth.dto.response.UserResponse;
import com.bloodflow.auth.entity.*;
import com.bloodflow.auth.enums.RoleName;
import com.bloodflow.auth.enums.UserStatus;
import com.bloodflow.auth.exception.*;
import com.bloodflow.auth.mapper.UserMapper;
import com.bloodflow.auth.repository.*;
import com.bloodflow.auth.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

/**
 * Handles registration, login, logout, token refresh,
 * password management and email verification.
 *
 * SOLID note (SRP): Only authentication and credential logic here.
 * Profile updates are in UserService. Admin operations are in AdminUserService.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    /**
     * Account lock policy for the PFA scenario:
     * after 3 wrong passwords, the account is locked for 15 minutes.
     */
    private static final int MAX_FAILED_LOGIN_ATTEMPTS = 3;
    private static final int LOCKOUT_MINUTES = 15;

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final RefreshTokenService refreshTokenService;
    private final EmailVerificationTokenRepository emailVerificationTokenRepository;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final AuditService auditService;
    private final EmailService emailService;
    private final UserMapper userMapper;

    @Value("${bloodflow.auth.email-verification-enabled}")
    private boolean emailVerificationEnabled;

    // ====================================================================
    // REGISTER
    // ====================================================================

    @Transactional
    public UserResponse register(RegisterRequest request) {
        // 1. Check email uniqueness
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Un compte avec cet email existe déjà.");
        }

        // 2. Only DONOR and PATIENT can register publicly
        RoleName roleName;
        try {
            roleName = RoleName.valueOf(request.getRole().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Rôle invalide: " + request.getRole());
        }

        if (roleName != RoleName.DONOR && roleName != RoleName.PATIENT) {
            throw new BadRequestException("L'inscription publique n'est disponible que pour les rôles DONOR et PATIENT.");
        }

        // 3. Check passwords match
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new BadRequestException("Les mots de passe ne correspondent pas.");
        }

        // 4. Find role entity
        Role role = roleRepository.findByName(roleName)
            .orElseThrow(() -> new BadRequestException("Rôle non trouvé dans la base."));

        // 5. Build user
        User user = User.builder()
            .firstName(request.getFirstName())
            .lastName(request.getLastName())
            .email(request.getEmail())
            .phoneNumber(request.getPhoneNumber())
            .passwordHash(passwordEncoder.encode(request.getPassword()))
            .city(request.getCity())
            .roles(Set.of(role))
            .build();

        // 6. Handle email verification
        if (emailVerificationEnabled) {
            user.setEmailVerified(false);
            user.setStatus(UserStatus.PENDING_EMAIL_VERIFICATION);
        } else {
            // Demo mode: auto-verify
            user.setEmailVerified(true);
            user.setStatus(UserStatus.ACTIVE);
        }

        User saved = userRepository.save(user);

        if (emailVerificationEnabled) {
            createAndSendVerificationEmail(saved);
        }

        auditService.logSuccess(saved.getEmail(), "REGISTER_SUCCESS",
            "New " + roleName + " registered");

        return userMapper.toResponse(saved);
    }

    // ====================================================================
    // LOGIN
    // ====================================================================

    @Transactional
    public AuthResponse login(LoginRequest request) {
        // 1. Find user
        User user = userRepository.findByEmail(request.getEmail())
            .orElseThrow(() -> {
                auditService.logFailure(request.getEmail(), "LOGIN_FAILED", "User not found");
                return new UnauthorizedException("Email ou mot de passe incorrect.");
            });

        // 2. Check account status BEFORE checking password
        // This avoids letting disabled/unverified users keep trying passwords.
        ensureAccountCanAuthenticate(user);

        // 3. Check password + lock the account after 3 failed attempts
        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            handleFailedLogin(user, request.getEmail());
        }

        // 4. Successful login: reset failed attempts, update last login
        resetFailedLoginState(user);
        user.setLastLoginAt(LocalDateTime.now());
        userRepository.save(user);

        // 5. Generate tokens
        String accessToken = jwtService.generateAccessToken(user);
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user);

        auditService.logSuccess(user.getEmail(), "LOGIN_SUCCESS", "Login successful");

        UserResponse userResponse = userMapper.toResponse(user);

        return AuthResponse.builder()
            .accessToken(accessToken)
            .token(accessToken) // Both fields for frontend compatibility
            .refreshToken(refreshToken.getToken())
            .expiresIn(jwtService.getAccessTokenExpirationSeconds())
            .user(userResponse)
            .mustChangePassword(user.isMustChangePassword())
            .nextAction(user.isMustChangePassword() ? "CHANGE_PASSWORD" : "GO_TO_DASHBOARD")
            .redirectTo(user.isMustChangePassword() ? "/change-password" : dashboardPathFor(user))
            .build();
    }

    // ====================================================================
    // REFRESH TOKEN
    // ====================================================================

    @Transactional
    public AuthResponse refreshToken(RefreshTokenRequest request) {
        RefreshToken refreshToken = refreshTokenService.validateRefreshToken(request.getRefreshToken());
        User user = refreshToken.getUser();

        ensureAccountCanAuthenticate(user);

        String newAccessToken = jwtService.generateAccessToken(user);

        return AuthResponse.builder()
            .accessToken(newAccessToken)
            .token(newAccessToken)
            .refreshToken(refreshToken.getToken())
            .expiresIn(jwtService.getAccessTokenExpirationSeconds())
            .user(userMapper.toResponse(user))
            .mustChangePassword(user.isMustChangePassword())
            .nextAction(user.isMustChangePassword() ? "CHANGE_PASSWORD" : "GO_TO_DASHBOARD")
            .redirectTo(user.isMustChangePassword() ? "/change-password" : dashboardPathFor(user))
            .build();
    }

    // ====================================================================
    // LOGOUT
    // ====================================================================

    @Transactional
    public void logout(String userEmail, LogoutRequest request) {
        if (request.getRefreshToken() != null) {
            refreshTokenService.revokeToken(request.getRefreshToken());
        }
        auditService.logSuccess(userEmail, "LOGOUT", "User logged out");
    }

    @Transactional
    public void logoutAll(UserPrincipal principal) {
        User user = userRepository.findById(principal.getId())
            .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé."));
        refreshTokenService.revokeAllTokensForUser(user);
        auditService.logSuccess(user.getEmail(), "LOGOUT", "User logged out from all devices");
    }

    // ====================================================================
    // CHANGE PASSWORD
    // ====================================================================

    @Transactional
    public void changePassword(UserPrincipal principal, ChangePasswordRequest request) {
        User user = userRepository.findById(principal.getId())
            .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé."));

        if (!passwordEncoder.matches(request.getOldPassword(), user.getPasswordHash())) {
            throw new BadRequestException("L'ancien mot de passe est incorrect.");
        }

        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new BadRequestException("Les nouveaux mots de passe ne correspondent pas.");
        }

        user.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
        user.setMustChangePassword(false);
        userRepository.save(user);

        auditService.logSuccess(user.getEmail(), "PASSWORD_CHANGED", "Password changed successfully");
    }

    // ====================================================================
    // FORGOT PASSWORD
    // ====================================================================

    @Transactional
    public void forgotPassword(ForgotPasswordRequest request) {
        // Always return the same message (security: don't reveal if email exists)
        userRepository.findByEmail(request.getEmail()).ifPresent(user -> {
            String token = UUID.randomUUID().toString();
            PasswordResetToken resetToken = PasswordResetToken.builder()
                .token(token)
                .user(user)
                .expiresAt(LocalDateTime.now().plusHours(2))
                .used(false)
                .build();
            passwordResetTokenRepository.save(resetToken);
            emailService.sendPasswordResetEmail(user.getEmail(), token);
            auditService.logSuccess(user.getEmail(), "PASSWORD_RESET_REQUESTED", "Reset token created");
        });
    }

    // ====================================================================
    // RESET PASSWORD
    // ====================================================================

    @Transactional
    public void resetPassword(ResetPasswordRequest request) {
        PasswordResetToken resetToken = passwordResetTokenRepository.findByToken(request.getToken())
            .orElseThrow(() -> new BadRequestException("Token de réinitialisation invalide."));

        if (resetToken.isUsed()) {
            throw new BadRequestException("Ce token a déjà été utilisé.");
        }
        if (LocalDateTime.now().isAfter(resetToken.getExpiresAt())) {
            throw new BadRequestException("Ce token a expiré.");
        }
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new BadRequestException("Les mots de passe ne correspondent pas.");
        }

        User user = resetToken.getUser();
        user.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
        user.setFailedLoginAttempts(0);
        user.setLockoutUntil(null);
        user.setMustChangePassword(false);
        userRepository.save(user);

        resetToken.setUsed(true);
        passwordResetTokenRepository.save(resetToken);

        auditService.logSuccess(user.getEmail(), "PASSWORD_RESET_SUCCESS", "Password reset completed");
    }

    // ====================================================================
    // EMAIL VERIFICATION
    // ====================================================================

    @Transactional
    public void verifyEmail(VerifyEmailRequest request) {
        EmailVerificationToken verificationToken = emailVerificationTokenRepository.findByToken(request.getToken())
            .orElseThrow(() -> new BadRequestException("Token de vérification invalide."));

        if (verificationToken.isUsed()) {
            throw new BadRequestException("Ce token a déjà été utilisé.");
        }
        if (LocalDateTime.now().isAfter(verificationToken.getExpiresAt())) {
            throw new BadRequestException("Ce token a expiré.");
        }

        User user = verificationToken.getUser();
        user.setEmailVerified(true);
        user.setStatus(UserStatus.ACTIVE);
        userRepository.save(user);

        verificationToken.setUsed(true);
        emailVerificationTokenRepository.save(verificationToken);

        auditService.logSuccess(user.getEmail(), "EMAIL_VERIFIED", "Email verified successfully");
    }

    @Transactional
    public void resendVerificationEmail(ResendVerificationEmailRequest request) {
        userRepository.findByEmail(request.getEmail()).ifPresent(user -> {
            if (!user.isEmailVerified()) {
                createAndSendVerificationEmail(user);
            }
        });
    }

    // ====================================================================
    // GET CURRENT USER
    // ====================================================================

    @Transactional(readOnly = true)
    public UserResponse getCurrentUser(UserPrincipal principal) {
        User user = userRepository.findById(principal.getId())
            .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé."));
        return userMapper.toResponse(user);
    }

    // ====================================================================
    // Private helpers
    // ====================================================================

    private void createAndSendVerificationEmail(User user) {
        String token = UUID.randomUUID().toString();
        EmailVerificationToken verificationToken = EmailVerificationToken.builder()
            .token(token)
            .user(user)
            .expiresAt(LocalDateTime.now().plusHours(24))
            .used(false)
            .build();
        emailVerificationTokenRepository.save(verificationToken);
        emailService.sendVerificationEmail(user.getEmail(), token);
    }

    /**
     * Checks if the account is allowed to authenticate or refresh a token.
     */
    private void ensureAccountCanAuthenticate(User user) {
        if (user.getStatus() == UserStatus.DISABLED) {
            throw new AccountDisabledException("Votre compte a été désactivé. Contactez l'administrateur.");
        }

        if (user.getStatus() == UserStatus.PENDING_EMAIL_VERIFICATION) {
            throw new EmailNotVerifiedException("Veuillez vérifier votre email avant de vous connecter.");
        }

        if (user.getLockoutUntil() != null) {
            if (LocalDateTime.now().isBefore(user.getLockoutUntil())) {
                throw new AccountLockedException(
                    "Compte temporairement bloqué après 3 tentatives échouées. Réessayez après "
                    + user.getLockoutUntil()
                );
            }

            // Lockout period expired: clean the lock before continuing.
            user.setFailedLoginAttempts(0);
            user.setLockoutUntil(null);
            userRepository.save(user);
        }
    }

    /**
     * Increments failed login attempts and locks the account at the third failure.
     */
    private void handleFailedLogin(User user, String email) {
        int attempts = user.getFailedLoginAttempts() + 1;
        user.setFailedLoginAttempts(attempts);

        if (attempts >= MAX_FAILED_LOGIN_ATTEMPTS) {
            LocalDateTime lockedUntil = LocalDateTime.now().plusMinutes(LOCKOUT_MINUTES);
            user.setLockoutUntil(lockedUntil);
            userRepository.save(user);

            auditService.logFailure(email, "LOGIN_LOCKED", "Account locked after 3 failed attempts until " + lockedUntil);
            throw new AccountLockedException(
                "Compte temporairement bloqué après 3 tentatives échouées. Réessayez dans "
                + LOCKOUT_MINUTES + " minutes."
            );
        }

        userRepository.save(user);
        auditService.logFailure(email, "LOGIN_FAILED", "Wrong password. Attempt " + attempts + "/" + MAX_FAILED_LOGIN_ATTEMPTS);

        int remaining = MAX_FAILED_LOGIN_ATTEMPTS - attempts;
        throw new UnauthorizedException("Email ou mot de passe incorrect. Tentatives restantes: " + remaining + ".");
    }

    /**
     * Clears failed login counters after a successful authentication.
     */
    private void resetFailedLoginState(User user) {
        user.setFailedLoginAttempts(0);
        user.setLockoutUntil(null);
    }

    /**
     * Gives the frontend a clear destination after login.
     * React can use response.data.data.redirectTo directly.
     */
    private String dashboardPathFor(User user) {
        if (user.getRoles().stream().anyMatch(role -> role.getName() == RoleName.ADMIN)) return "/dashboard/admin";
        if (user.getRoles().stream().anyMatch(role -> role.getName() == RoleName.DONOR)) return "/dashboard/donor";
        if (user.getRoles().stream().anyMatch(role -> role.getName() == RoleName.PATIENT)) return "/dashboard/patient";
        if (user.getRoles().stream().anyMatch(role -> role.getName() == RoleName.DOCTOR)) return "/dashboard/doctor";
        if (user.getRoles().stream().anyMatch(role -> role.getName() == RoleName.LAB_TECHNICIAN)) return "/dashboard/lab-technician";
        if (user.getRoles().stream().anyMatch(role -> role.getName() == RoleName.BIOLOGIST)) return "/dashboard/biologist";
        if (user.getRoles().stream().anyMatch(role -> role.getName() == RoleName.DELIVERY_AGENT)) return "/dashboard/delivery-agent";
        if (user.getRoles().stream().anyMatch(role -> role.getName() == RoleName.PROMOTER)) return "/dashboard/promoter";
        if (user.getRoles().stream().anyMatch(role -> role.getName() == RoleName.STAFF)) return "/dashboard/staff";
        return "/dashboard";
    }

}
