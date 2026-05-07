package com.bloodflow.medical.service;
import com.bloodflow.medical.entity.Patient;
import com.bloodflow.medical.repository.PatientRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Optional;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PatientServiceTest {
    @Mock private PatientRepository patientRepository;

    @Test
    void findById_retournePatient() {
        Patient p = new Patient(); p.setId(1L); p.setNom("Alaoui"); p.setPrenom("Fatima");
        when(patientRepository.findById(1L)).thenReturn(Optional.of(p));
        Optional<Patient> result = patientRepository.findById(1L);
        assertThat(result).isPresent();
        assertThat(result.get().getNom()).isEqualTo("Alaoui");
    }

    @Test
    void findById_nonTrouve_retourneVide() {
        when(patientRepository.findById(99L)).thenReturn(Optional.empty());
        assertThat(patientRepository.findById(99L)).isEmpty();
    }

    @Test
    void save_sauvegarde() {
        Patient p = new Patient(); p.setNom("Benali"); p.setPrenom("Youssef"); p.setEmail("y@test.com");
        when(patientRepository.save(p)).thenReturn(p);
        Patient saved = patientRepository.save(p);
        assertThat(saved.getNom()).isEqualTo("Benali");
        verify(patientRepository).save(p);
    }
}
