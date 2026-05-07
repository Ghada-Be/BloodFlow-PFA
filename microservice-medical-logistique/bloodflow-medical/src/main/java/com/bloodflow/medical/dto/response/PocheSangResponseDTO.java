package com.bloodflow.medical.dto.response;
import com.bloodflow.medical.entity.StatutPoche;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;
@Data
public class PocheSangResponseDTO {
    private Long id;
    private String numeroPoche;
    private String groupeSanguin;
    private String typeProduit;
    private Integer volumeMl;
    private StatutPoche statut;
    private LocalDate dateCollecte;
    private LocalDate dateExpiration;
    private String centreCollecte;
    private Long stockId;
    private LocalDateTime dateCreation;
}
