package com.bloodflow.medical.integration;
import com.bloodflow.medical.entity.Patient;
import com.bloodflow.medical.repository.PatientRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import static org.assertj.core.api.Assertions.*;
@SpringBootTest
@ActiveProfiles("test")
@Transactional
class PatientIntegrationTest {
    @Autowired private PatientRepository patientRepository;
    @Test void createAndFind() {
        Patient p = new Patient();
        p.setNom("Integration"); p.setPrenom("Test"); p.setEmail("integ@test.com");
        Patient saved = patientRepository.save(p);
        assertThat(saved.getId()).isNotNull();
        assertThat(patientRepository.findByEmail("integ@test.com")).isPresent();
    }
}
