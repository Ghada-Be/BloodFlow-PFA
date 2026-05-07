package com.bloodflow.medical.repository;
import com.bloodflow.medical.entity.Biologiste;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
public interface BiologisteRepository extends JpaRepository<Biologiste, Long> {
    Optional<Biologiste> findByEmail(String email);
}
