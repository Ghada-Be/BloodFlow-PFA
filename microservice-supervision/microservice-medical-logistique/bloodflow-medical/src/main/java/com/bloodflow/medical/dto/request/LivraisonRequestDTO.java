package com.bloodflow.medical.dto.request;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDateTime;
@Data
public class LivraisonRequestDTO {
    @NotNull(message = "L'ID de la commande est obligatoire")
    private Long commandeId;
    private Long livreurId;
    @NotBlank(message = "L'adresse de livraison est obligatoire")
    private String adresseLivraison;
    private Double temperatureTransport;
    private String notes;
    private LocalDateTime dateDepart;
}
