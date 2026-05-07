package com.bloodflow.medical.repository;
import com.bloodflow.medical.entity.Livreur;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
public interface LivreurRepository extends JpaRepository<Livreur, Long> {
    Optional<Livreur> findByEmail(String email);
}
