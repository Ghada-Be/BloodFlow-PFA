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
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                // Swagger — accessible sans authentification
                .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/swagger-ui.html").permitAll()
                // Health check
                .requestMatchers("/actuator/health").permitAll()
                // Lecture libre pour tous les utilisateurs authentifiés
                .requestMatchers(HttpMethod.GET, "/api/**").authenticated()
                // Création/modification : médecins, biologistes, personnel
                .requestMatchers(HttpMethod.POST, "/api/dossiers-medicaux/**").hasAnyRole("MEDECIN", "ADMIN", "PERSONNEL_CENTRE")
                .requestMatchers(HttpMethod.POST, "/api/prescriptions/**").hasAnyRole("MEDECIN", "ADMIN")
                .requestMatchers(HttpMethod.POST, "/api/analyses-sang/**").hasAnyRole("TECHNICIEN", "BIOLOGISTE", "ADMIN")
                .requestMatchers(HttpMethod.POST, "/api/resultats-biologiques/**").hasAnyRole("BIOLOGISTE", "ADMIN")
                .requestMatchers(HttpMethod.POST, "/api/commandes-sang/**").hasAnyRole("MEDECIN", "PERSONNEL_CENTRE", "ADMIN")
                .requestMatchers(HttpMethod.POST, "/api/stocks/**").hasAnyRole("PERSONNEL_CENTRE", "ADMIN")
                .requestMatchers(HttpMethod.POST, "/api/poches-sang/**").hasAnyRole("PERSONNEL_CENTRE", "ADMIN")
                .requestMatchers(HttpMethod.POST, "/api/livraisons/**").hasAnyRole("LIVREUR", "PERSONNEL_CENTRE", "ADMIN")
                // Suppression : admin seulement
                .requestMatchers(HttpMethod.DELETE, "/api/**").hasRole("ADMIN")
                // Tout le reste : authentifié
                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
