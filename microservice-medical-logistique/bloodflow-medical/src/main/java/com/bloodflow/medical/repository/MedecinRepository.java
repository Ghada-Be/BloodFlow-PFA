package com.bloodflow.medical.repository;
import com.bloodflow.medical.entity.Medecin;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;
public interface MedecinRepository extends JpaRepository<Medecin, Long> {
    Optional<Medecin> findByEmail(String email);
    List<Medecin> findBySpecialite(String specialite);
}
