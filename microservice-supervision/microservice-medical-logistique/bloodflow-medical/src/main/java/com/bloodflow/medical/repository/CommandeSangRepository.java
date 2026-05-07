package com.bloodflow.medical.repository;
import com.bloodflow.medical.entity.CommandeSang;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;
public interface CommandeSangRepository extends JpaRepository<CommandeSang, Long> {
    List<CommandeSang> findByStatut(String statut);
    List<CommandeSang> findByGroupeSanguin(String groupeSanguin);
    Optional<CommandeSang> findByNumeroCommande(String numeroCommande);
    boolean existsByNumeroCommande(String numeroCommande);
}
