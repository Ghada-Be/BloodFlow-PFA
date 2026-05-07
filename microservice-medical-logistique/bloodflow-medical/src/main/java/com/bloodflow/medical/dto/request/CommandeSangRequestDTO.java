package com.bloodflow.medical.dto.request;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDateTime;
@Data
public class CommandeSangRequestDTO {
    private Long prescriptionId;
    @NotBlank(message = "Le groupe sanguin est obligatoire")
    private String groupeSanguin;
    @NotBlank(message = "Le type de produit est obligatoire")
    private String typeProduit;
    @NotNull @Min(value = 1, message = "La quantité doit être supérieure à 0")
    private Integer quantite;
    private Boolean urgence = false;
    private String hopitalDemandeur;
    private String notes;
    private LocalDateTime dateLivraisonSouhaitee;
}
