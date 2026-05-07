package com.bloodflow.medical.dto.request;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
@Data
public class ResultatBiologiqueRequestDTO {
    @NotNull(message = "L'ID de l'analyse est obligatoire")
    private Long analyseId;
    private Long biologisteId;
    private Double valeurHemoglobine;
    private Double valeurHematocrite;
    private Double nombreGlobulesRouges;
    private Double nombreGlobulesBlancs;
    private Double nombrePlaquettes;
    private String groupeSanguinConfirme;
    private String observations;
    private Boolean valide = false;
}
