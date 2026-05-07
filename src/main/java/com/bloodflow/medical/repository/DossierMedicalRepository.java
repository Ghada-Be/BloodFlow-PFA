package com.bloodflow.medical.repository;
import com.bloodflow.medical.entity.DossierMedical;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;
public interface DossierMedicalRepository extends JpaRepository<DossierMedical, Long> {
    Optional<DossierMedical> findByNumeroDossier(String numeroDossier);
    List<DossierMedical> findByPatientId(Long patientId);
    boolean existsByNumeroDossier(String numeroDossier);
}
