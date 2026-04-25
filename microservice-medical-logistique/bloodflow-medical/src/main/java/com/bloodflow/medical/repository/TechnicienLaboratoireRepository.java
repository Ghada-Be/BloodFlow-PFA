package com.bloodflow.medical.repository;
import com.bloodflow.medical.entity.TechnicienLaboratoire;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
public interface TechnicienLaboratoireRepository extends JpaRepository<TechnicienLaboratoire, Long> {
    Optional<TechnicienLaboratoire> findByEmail(String email);
}
