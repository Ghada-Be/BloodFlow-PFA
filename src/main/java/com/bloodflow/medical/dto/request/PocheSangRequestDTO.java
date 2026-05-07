package com.bloodflow.medical.dto.request;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import java.time.LocalDate;
@Data
public class PocheSangRequestDTO {
    @NotBlank(message = "Le numéro de poche est obligatoire")
    private String numeroPoche;
    @NotBlank(message = "Le groupe sanguin est obligatoire")
    private String groupeSanguin;
    @NotBlank(message = "Le type de produit est obligatoire")
    private String typeProduit;
    private Integer volumeMl;
    private LocalDate dateCollecte;
    private LocalDate dateExpiration;
    private String centreCollecte;
    private Long stockId;
}
