package com.bloodflow.medical.repository;
import com.bloodflow.medical.entity.Stock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;
import java.util.Optional;
public interface StockRepository extends JpaRepository<Stock, Long> {
    Optional<Stock> findByCentreSangAndGroupeSanguinAndTypeProduit(String centre, String groupe, String type);
    List<Stock> findByCentreSang(String centreSang);
    List<Stock> findByGroupeSanguin(String groupeSanguin);
    @Query("SELECT s FROM Stock s WHERE s.quantiteDisponible <= s.seuilAlerte")
    List<Stock> findStocksBelowThreshold();
}
