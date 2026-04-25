package com.bloodflow.medical.dto.request;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
@Data
public class DossierMedicalRequestDTO {
    @NotNull(message = "L'ID du patient est obligatoire")
    private Long patientId;
    @NotBlank(message = "Le numéro de dossier est obligatoire")
    private String numeroDossier;
    private String antecedentsMedicaux;
    private String allergies;
    private String groupeSanguin;
    private String notes;
}
