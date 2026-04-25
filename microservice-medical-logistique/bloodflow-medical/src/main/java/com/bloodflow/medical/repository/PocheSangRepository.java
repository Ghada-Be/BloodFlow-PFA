package com.bloodflow.medical.repository;
import com.bloodflow.medical.entity.PocheSang;
import com.bloodflow.medical.entity.StatutPoche;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;
public interface PocheSangRepository extends JpaRepository<PocheSang, Long> {
    List<PocheSang> findByStatut(StatutPoche statut);
    List<PocheSang> findByGroupeSanguinAndStatut(String groupeSanguin, StatutPoche statut);
    Optional<PocheSang> findByNumeroPoche(String numeroPoche);
    boolean existsByNumeroPoche(String numeroPoche);
}
