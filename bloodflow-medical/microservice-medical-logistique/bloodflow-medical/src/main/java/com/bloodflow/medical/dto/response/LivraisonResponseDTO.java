package com.bloodflow.medical.dto.response;
import lombok.Data;
import java.time.LocalDateTime;
@Data
public class LivraisonResponseDTO {
    private Long id;
    private String numeroLivraison;
    private Long commandeId;
    private String numeroCommande;
    private Long livreurId;
    private String nomLivreur;
    private String adresseLivraison;
    private String statut;
    private Double temperatureTransport;
    private String notes;
    private LocalDateTime dateDepart;
    private LocalDateTime dateLivraison;
    private LocalDateTime dateCreation;
}
