package com.bloodflow.medical.repository;
import com.bloodflow.medical.entity.Patient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import java.util.Optional;
import static org.assertj.core.api.Assertions.*;
@DataJpaTest
@ActiveProfiles("test")
class PatientRepositoryTest {
    @Autowired private PatientRepository patientRepository;
    @Test void save_etFindById() {
        Patient p = new Patient();
        p.setNom("Alaoui"); p.setPrenom("Fatima"); p.setEmail("fatima@test.com");
        Patient saved = patientRepository.save(p);
        assertThat(saved.getId()).isNotNull();
        Optional<Patient> found = patientRepository.findById(saved.getId());
        assertThat(found).isPresent();
        assertThat(found.get().getNom()).isEqualTo("Alaoui");
    }
    @Test void findByEmail_trouveBien() {
        Patient p = new Patient();
        p.setNom("Benali"); p.setPrenom("Omar"); p.setEmail("omar@test.com");
        patientRepository.save(p);
        Optional<Patient> found = patientRepository.findByEmail("omar@test.com");
        assertThat(found).isPresent();
    }
    @Test void delete_supprimeBien() {
        Patient p = new Patient();
        p.setNom("Test"); p.setPrenom("Test"); p.setEmail("del@test.com");
        Patient saved = patientRepository.save(p);
        patientRepository.deleteById(saved.getId());
        assertThat(patientRepository.findById(saved.getId())).isEmpty();
    }
}
