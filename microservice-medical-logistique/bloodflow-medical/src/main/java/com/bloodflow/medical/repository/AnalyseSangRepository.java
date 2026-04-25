package com.bloodflow.medical.repository;
import com.bloodflow.medical.entity.AnalyseSang;
import com.bloodflow.medical.entity.EtatAnalyse;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface AnalyseSangRepository extends JpaRepository<AnalyseSang, Long> {
    List<AnalyseSang> findByEtat(EtatAnalyse etat);
    List<AnalyseSang> findByDossierMedicalId(Long dossierId);
    boolean existsByReference(String reference);
}
