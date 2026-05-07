package com.bloodflow.medical.integration;
import com.bloodflow.medical.entity.*;
import com.bloodflow.medical.repository.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import static org.assertj.core.api.Assertions.*;
@SpringBootTest
@ActiveProfiles("test")
@Transactional
class AnalyseSangIntegrationTest {
    @Autowired private AnalyseSangRepository analyseSangRepository;
    @Autowired private DossierMedicalRepository dossierMedicalRepository;
    @Autowired private PatientRepository patientRepository;
    @Test void createAnalyseAvecDossier() {
        Patient p = new Patient(); p.setNom("Test"); p.setPrenom("Test"); p.setEmail("at@test.com");
        patientRepository.save(p);
        DossierMedical d = new DossierMedical(); d.setNumeroDossier("D-INT-001"); d.setPatient(p);
        dossierMedicalRepository.save(d);
        AnalyseSang a = new AnalyseSang(); a.setTypeAnalyse("NFS"); a.setEtat(EtatAnalyse.EN_ATTENTE); a.setDossierMedical(d);
        AnalyseSang saved = analyseSangRepository.save(a);
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getEtat()).isEqualTo(EtatAnalyse.EN_ATTENTE);
    }
}
