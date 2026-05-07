package com.bloodflow.auth.seed;

import com.bloodflow.auth.entity.Role;
import com.bloodflow.auth.entity.User;
import com.bloodflow.auth.enums.RoleName;
import com.bloodflow.auth.enums.UserStatus;
import com.bloodflow.auth.repository.RoleRepository;
import com.bloodflow.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Set;

/**
 * Creates the default ADMIN user at startup if it doesn't already exist.
 *
 * @Order(2) ensures this runs AFTER RoleSeeder (which creates the ADMIN role).
 *
 * Default credentials (change in application.yml for production):
 *   email:    admin@bloodflow.ma
 *   password: Admin123@
 */
@Slf4j
@Component
@Order(2)
@RequiredArgsConstructor
public class AdminSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${bloodflow.admin.email}")
    private String adminEmail;

    @Value("${bloodflow.admin.password}")
    private String adminPassword;

    @Value("${bloodflow.admin.first-name}")
    private String adminFirstName;

    @Value("${bloodflow.admin.last-name}")
    private String adminLastName;

    @Override
    public void run(String... args) {
        if (userRepository.existsByEmail(adminEmail)) {
            log.info("[Seeder] Admin user '{}' already exists. Skipping.", adminEmail);
            return;
        }

        Role adminRole = roleRepository.findByName(RoleName.ADMIN)
            .orElseThrow(() -> new IllegalStateException(
                "ADMIN role not found. Make sure RoleSeeder runs before AdminSeeder."));

        User admin = User.builder()
            .firstName(adminFirstName)
            .lastName(adminLastName)
            .email(adminEmail)
            .passwordHash(passwordEncoder.encode(adminPassword))
            .emailVerified(true)
            .status(UserStatus.ACTIVE)
            .mustChangePassword(false)
            .roles(Set.of(adminRole))
            .build();

        userRepository.save(admin);

        log.info("[Seeder] ================================================");
        log.info("[Seeder] Default admin created:");
        log.info("[Seeder]   Email:    {}", adminEmail);
        log.info("[Seeder]   Password: {}", adminPassword);
        log.info("[Seeder]   Role:     ADMIN");
        log.info("[Seeder] ================================================");
    }
}
