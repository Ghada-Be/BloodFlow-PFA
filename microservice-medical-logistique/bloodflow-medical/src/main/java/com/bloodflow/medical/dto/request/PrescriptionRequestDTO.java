package com.bloodflow.medical.dto.request;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDate;
@Data
public class PrescriptionRequestDTO {
    @NotNull(message = "L'ID du médecin est obligatoire")
    private Long medecinId;
    @NotNull(message = "L'ID du dossier médical est obligatoire")
    private Long dossierMedicalId;
    @NotBlank(message = "Le type de produit sanguin est obligatoire")
    private String typeProduitSanguin;
    @Min(value = 1) private Integer quantite;
    private String groupeSanguinRequis;
    private Boolean urgence = false;
    private String motif;
    private LocalDate datePrescription;
}
