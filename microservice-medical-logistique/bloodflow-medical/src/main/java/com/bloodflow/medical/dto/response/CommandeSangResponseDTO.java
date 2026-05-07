package com.bloodflow.medical.dto.response;
import lombok.Data;
import java.time.LocalDateTime;
@Data
public class CommandeSangResponseDTO {
    private Long id;
    private String numeroCommande;
    private Long prescriptionId;
    private String groupeSanguin;
    private String typeProduit;
    private Integer quantite;
    private String statut;
    private Boolean urgence;
    private String hopitalDemandeur;
    private String notes;
    private LocalDateTime dateCommande;
    private LocalDateTime dateLivraisonSouhaitee;
}
