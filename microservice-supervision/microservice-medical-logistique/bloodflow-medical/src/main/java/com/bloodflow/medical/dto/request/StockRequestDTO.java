package com.bloodflow.medical.dto.request;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;
@Data
public class StockRequestDTO {
    @NotBlank(message = "Le centre de sang est obligatoire")
    private String centreSang;
    @NotBlank(message = "Le groupe sanguin est obligatoire")
    private String groupeSanguin;
    @NotBlank(message = "Le type de produit est obligatoire")
    private String typeProduit;
    @PositiveOrZero private Integer quantiteDisponible = 0;
    @PositiveOrZero private Integer seuilAlerte = 5;
}
