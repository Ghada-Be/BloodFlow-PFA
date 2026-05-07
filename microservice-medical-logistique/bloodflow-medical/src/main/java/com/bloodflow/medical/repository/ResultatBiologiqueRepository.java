package com.bloodflow.medical.repository;
import com.bloodflow.medical.entity.ResultatBiologique;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;
public interface ResultatBiologiqueRepository extends JpaRepository<ResultatBiologique, Long> {
    Optional<ResultatBiologique> findByAnalyseId(Long analyseId);
    List<ResultatBiologique> findByBiologisteId(Long biologisteId);
    List<ResultatBiologique> findByValide(Boolean valide);
}
