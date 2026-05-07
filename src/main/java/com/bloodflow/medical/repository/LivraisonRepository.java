package com.bloodflow.medical.repository;
import com.bloodflow.medical.entity.Livraison;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface LivraisonRepository extends JpaRepository<Livraison, Long> {
    List<Livraison> findByStatut(String statut);
    List<Livraison> findByLivreurId(Long livreurId);
}
