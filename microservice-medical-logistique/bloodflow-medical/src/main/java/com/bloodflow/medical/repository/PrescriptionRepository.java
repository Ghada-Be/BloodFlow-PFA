package com.bloodflow.medical.repository;
import com.bloodflow.medical.entity.Prescription;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface PrescriptionRepository extends JpaRepository<Prescription, Long> {
    List<Prescription> findByMedecinId(Long medecinId);
    List<Prescription> findByDossierMedicalId(Long dossierId);
    List<Prescription> findByUrgence(Boolean urgence);
}
