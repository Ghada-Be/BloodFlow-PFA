package com.bloodflow.auth.security;

import com.bloodflow.auth.entity.User;
import com.bloodflow.auth.enums.UserStatus;
import com.bloodflow.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * Spring Security calls this service to load a user by email.
 *
 * Security fix:
 * - Disabled users are not loaded as authenticated users.
 * - Temporarily locked users are not loaded as authenticated users.
 * This means old JWT access tokens stop working immediately after account disable/lock.
 */
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

        if (user.getStatus() == UserStatus.DISABLED) {
            throw new DisabledException("User account is disabled: " + email);
        }

        if (user.getLockoutUntil() != null && LocalDateTime.now().isBefore(user.getLockoutUntil())) {
            throw new LockedException("User account is temporarily locked: " + email);
        }

        return new UserPrincipal(user);
    }
}
