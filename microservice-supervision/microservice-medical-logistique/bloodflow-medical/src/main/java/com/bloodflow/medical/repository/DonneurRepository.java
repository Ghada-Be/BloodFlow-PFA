package com.bloodflow.medical.repository;
import com.bloodflow.medical.entity.Donneur;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;
public interface DonneurRepository extends JpaRepository<Donneur, Long> {
    Optional<Donneur> findByEmail(String email);
    List<Donneur> findByGroupeSanguin(String groupeSanguin);
    List<Donneur> findByEligibleDon(Boolean eligible);
}
