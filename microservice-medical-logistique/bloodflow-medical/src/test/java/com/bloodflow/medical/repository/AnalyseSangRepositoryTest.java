package com.bloodflow.medical.repository;
import com.bloodflow.medical.entity.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import java.util.List;
import static org.assertj.core.api.Assertions.*;
@DataJpaTest
@ActiveProfiles("test")
class AnalyseSangRepositoryTest {
    @Autowired private AnalyseSangRepository analyseSangRepository;
    @Autowired private DossierMedicalRepository dossierMedicalRepository;
    @Autowired private PatientRepository patientRepository;

    private DossierMedical createDossier() {
        Patient p = new Patient();
        p.setNom("Test"); p.setPrenom("Test"); p.setEmail("t@t.com");
        patientRepository.save(p);
        DossierMedical d = new DossierMedical();
        d.setNumeroDossier("DOS-001"); d.setPatient(p);
        return dossierMedicalRepository.save(d);
    }

    @Test void save_etFindByEtat() {
        DossierMedical dossier = createDossier();
        AnalyseSang a = new AnalyseSang();
        a.setTypeAnalyse("NFS"); a.setEtat(EtatAnalyse.EN_ATTENTE); a.setDossierMedical(dossier);
        analyseSangRepository.save(a);
        List<AnalyseSang> result = analyseSangRepository.findByEtat(EtatAnalyse.EN_ATTENTE);
        assertThat(result).isNotEmpty();
    }

    @Test void findByDossierMedical_trouveBien() {
        DossierMedical dossier = createDossier();
        AnalyseSang a = new AnalyseSang();
        a.setTypeAnalyse("Groupe sanguin"); a.setEtat(EtatAnalyse.EN_COURS); a.setDossierMedical(dossier);
        analyseSangRepository.save(a);
        assertThat(analyseSangRepository.findByDossierMedicalId(dossier.getId())).hasSize(1);
    }
}
