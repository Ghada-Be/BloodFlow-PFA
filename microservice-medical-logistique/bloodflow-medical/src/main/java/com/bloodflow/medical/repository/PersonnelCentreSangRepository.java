package com.bloodflow.medical.repository;
import com.bloodflow.medical.entity.PersonnelCentreSang;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface PersonnelCentreSangRepository extends JpaRepository<PersonnelCentreSang, Long> {
    List<PersonnelCentreSang> findByCentreSang(String centreSang);
}
