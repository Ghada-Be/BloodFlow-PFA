package com.bloodflow.auth.seed;

import com.bloodflow.auth.entity.Role;
import com.bloodflow.auth.enums.RoleName;
import com.bloodflow.auth.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Creates all roles in the database at startup if they don't already exist.
 *
 * This runs automatically when Spring Boot starts.
 * @Order(1) ensures roles are created BEFORE the admin user (which needs roles).
 *
 * WHY CommandLineRunner?
 * Spring Boot calls the run() method once after the application starts.
 * This is the standard way to do database initialization in Spring Boot.
 */
@Slf4j
@Component
@Order(1)
@RequiredArgsConstructor
public class RoleSeeder implements CommandLineRunner {

    private final RoleRepository roleRepository;

    private static final Map<RoleName, String> ROLE_DESCRIPTIONS = Map.of(
        RoleName.ADMIN,           "System administrator with full access",
        RoleName.DONOR,           "Blood donor — can schedule donations",
        RoleName.PATIENT,         "Patient who needs blood",
        RoleName.DOCTOR,          "Medical doctor — manages patient records",
        RoleName.STAFF,           "General hospital staff",
        RoleName.LAB_TECHNICIAN,  "Laboratory technician — manages blood tests",
        RoleName.BIOLOGIST,       "Biologist — validates blood bags",
        RoleName.DELIVERY_AGENT,  "Delivers blood bags to hospitals",
        RoleName.PROMOTER,        "Promotes donation campaigns"
    );

    @Override
    public void run(String... args) {
        int created = 0;
        for (Map.Entry<RoleName, String> entry : ROLE_DESCRIPTIONS.entrySet()) {
            if (!roleRepository.existsByName(entry.getKey())) {
                roleRepository.save(Role.builder()
                    .name(entry.getKey())
                    .description(entry.getValue())
                    .build());
                created++;
            }
        }
        if (created > 0) {
            log.info("[Seeder] Created {} role(s).", created);
        } else {
            log.info("[Seeder] All roles already exist. Skipping.");
        }
    }
}
