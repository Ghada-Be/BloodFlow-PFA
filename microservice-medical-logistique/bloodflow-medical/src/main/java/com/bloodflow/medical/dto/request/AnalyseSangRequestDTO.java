package com.bloodflow.medical.dto.request;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
@Data
public class AnalyseSangRequestDTO {
    @NotNull(message = "L'ID du dossier médical est obligatoire")
    private Long dossierMedicalId;
    private Long technicienId;
    @NotBlank(message = "Le type d'analyse est obligatoire")
    private String typeAnalyse;
    private String description;
    private String reference;
}
