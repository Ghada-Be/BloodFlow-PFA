package com.bloodflow.medical.dto.response;
import com.bloodflow.medical.entity.EtatAnalyse;
import lombok.Data;
import java.time.LocalDateTime;
@Data
public class AnalyseSangResponseDTO {
    private Long id;
    private String reference;
    private Long dossierMedicalId;
    private Long technicienId;
    private String nomTechnicien;
    private String typeAnalyse;
    private String description;
    private EtatAnalyse etat;
    private LocalDateTime dateDemande;
    private LocalDateTime dateRealisation;
}
