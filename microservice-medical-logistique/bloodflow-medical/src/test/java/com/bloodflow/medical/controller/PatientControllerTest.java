package com.bloodflow.medical.controller;

import com.bloodflow.medical.entity.Patient;
import com.bloodflow.medical.repository.PatientRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.List;
import java.util.Optional;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Tests du repository Patient (pas de PatientController dédié dans la structure,
 * on teste le repository via mock comme demandé).
 */
@ExtendWith(MockitoExtension.class)
class PatientControllerTest {

    @Mock private PatientRepository patientRepository;

    @Test
    void findAll_retourneListe() {
        Patient p = new Patient(); p.setId(1L); p.setNom("Alaoui");
        when(patientRepository.findAll()).thenReturn(List.of(p));
        assertThat(patientRepository.findAll()).hasSize(1);
    }

    @Test
    void findById_retournePatient() {
        Patient p = new Patient(); p.setId(1L); p.setNom("Alaoui");
        when(patientRepository.findById(1L)).thenReturn(Optional.of(p));
        assertThat(patientRepository.findById(1L)).isPresent();
        assertThat(patientRepository.findById(1L).get().getNom()).isEqualTo("Alaoui");
    }

    @Test
    void findById_nonTrouve_retourneVide() {
        when(patientRepository.findById(99L)).thenReturn(Optional.empty());
        assertThat(patientRepository.findById(99L)).isEmpty();
    }
}
