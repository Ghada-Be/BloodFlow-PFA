package com.bloodflow.medical.config;

import com.bloodflow.medical.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .cors(cors -> {})
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                // Preflight CORS
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                // Swagger / OpenAPI
                .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/swagger-ui.html").permitAll()

                // Health checks
                .requestMatchers("/api/health", "/api/medical/health", "/actuator/health").permitAll()

                // Dashboard: selon rôle connecté
                .requestMatchers(HttpMethod.GET, "/api/dashboard/admin/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.GET, "/api/dashboard/medecin/**").hasAnyRole("DOCTOR", "ADMIN")
                .requestMatchers(HttpMethod.GET, "/api/dashboard/technicien/**").hasAnyRole("LAB_TECHNICIAN", "ADMIN")
                .requestMatchers(HttpMethod.GET, "/api/dashboard/biologiste/**").hasAnyRole("BIOLOGIST", "ADMIN")
                .requestMatchers(HttpMethod.GET, "/api/dashboard/personnel/**").hasAnyRole("STAFF", "ADMIN")
                .requestMatchers(HttpMethod.GET, "/api/dashboard/livreur/**").hasAnyRole("DELIVERY_AGENT", "ADMIN")
                .requestMatchers(HttpMethod.GET, "/api/dashboard/donneur/**").hasAnyRole("DONOR", "ADMIN")
                .requestMatchers(HttpMethod.GET, "/api/dashboard/patient/**").hasAnyRole("PATIENT", "DOCTOR", "ADMIN")
                .requestMatchers(HttpMethod.GET, "/api/dashboard/agent/**", "/api/dashboard/agent").hasAnyRole("PROMOTER", "ADMIN")
                .requestMatchers(HttpMethod.GET, "/api/dashboard/hopital/**", "/api/dashboard/hopital").hasAnyRole("STAFF", "DOCTOR", "ADMIN")

                // Lecture médicale/logistique: tout utilisateur authentifié
                .requestMatchers(HttpMethod.GET, "/api/**").authenticated()

                // Création / modification selon les rôles MS1 exacts
                .requestMatchers(HttpMethod.POST, "/api/dossiers-medicaux/**").hasAnyRole("DOCTOR", "ADMIN", "STAFF")
                .requestMatchers(HttpMethod.PUT, "/api/dossiers-medicaux/**").hasAnyRole("DOCTOR", "ADMIN", "STAFF")
                .requestMatchers(HttpMethod.PATCH, "/api/dossiers-medicaux/**").hasAnyRole("DOCTOR", "ADMIN", "STAFF")

                .requestMatchers(HttpMethod.POST, "/api/prescriptions/**").hasAnyRole("DOCTOR", "ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/prescriptions/**").hasAnyRole("DOCTOR", "ADMIN")
                .requestMatchers(HttpMethod.PATCH, "/api/prescriptions/**").hasAnyRole("DOCTOR", "ADMIN")

                .requestMatchers(HttpMethod.POST, "/api/analyses-sang/**").hasAnyRole("LAB_TECHNICIAN", "BIOLOGIST", "ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/analyses-sang/**").hasAnyRole("LAB_TECHNICIAN", "BIOLOGIST", "ADMIN")
                .requestMatchers(HttpMethod.PATCH, "/api/analyses-sang/**").hasAnyRole("LAB_TECHNICIAN", "BIOLOGIST", "ADMIN")

                .requestMatchers(HttpMethod.POST, "/api/resultats-biologiques/**").hasAnyRole("BIOLOGIST", "ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/resultats-biologiques/**").hasAnyRole("BIOLOGIST", "ADMIN")
                .requestMatchers(HttpMethod.PATCH, "/api/resultats-biologiques/**").hasAnyRole("BIOLOGIST", "ADMIN")

                .requestMatchers(HttpMethod.POST, "/api/commandes-sang/**").hasAnyRole("DOCTOR", "STAFF", "ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/commandes-sang/**").hasAnyRole("DOCTOR", "STAFF", "ADMIN")
                .requestMatchers(HttpMethod.PATCH, "/api/commandes-sang/**").hasAnyRole("DOCTOR", "STAFF", "ADMIN")

                .requestMatchers(HttpMethod.POST, "/api/stocks/**").hasAnyRole("STAFF", "ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/stocks/**").hasAnyRole("STAFF", "ADMIN")
                .requestMatchers(HttpMethod.PATCH, "/api/stocks/**").hasAnyRole("STAFF", "ADMIN")

                .requestMatchers(HttpMethod.POST, "/api/poches-sang/**").hasAnyRole("STAFF", "LAB_TECHNICIAN", "BIOLOGIST", "ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/poches-sang/**").hasAnyRole("STAFF", "LAB_TECHNICIAN", "BIOLOGIST", "ADMIN")
                .requestMatchers(HttpMethod.PATCH, "/api/poches-sang/**").hasAnyRole("STAFF", "LAB_TECHNICIAN", "BIOLOGIST", "ADMIN")

                .requestMatchers(HttpMethod.POST, "/api/livraisons/**").hasAnyRole("DELIVERY_AGENT", "STAFF", "ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/livraisons/**").hasAnyRole("DELIVERY_AGENT", "STAFF", "ADMIN")
                .requestMatchers(HttpMethod.PATCH, "/api/livraisons/**").hasAnyRole("DELIVERY_AGENT", "STAFF", "ADMIN")

                // Suppression: admin seulement
                .requestMatchers(HttpMethod.DELETE, "/api/**").hasRole("ADMIN")

                // Le reste: authentifié
                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
