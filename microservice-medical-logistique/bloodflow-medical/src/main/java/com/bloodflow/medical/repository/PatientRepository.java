package com.bloodflow.medical.repository;
import com.bloodflow.medical.entity.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;
public interface PatientRepository extends JpaRepository<Patient, Long> {
    Optional<Patient> findByEmail(String email);
    Optional<Patient> findByNumeroSecuriteSociale(String nss);
    List<Patient> findByGroupeSanguin(String groupeSanguin);
}
