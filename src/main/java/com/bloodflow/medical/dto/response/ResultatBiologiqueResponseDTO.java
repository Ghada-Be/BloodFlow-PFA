package com.bloodflow.medical.dto.response;
import lombok.Data;
import java.time.LocalDateTime;
@Data
public class ResultatBiologiqueResponseDTO {
    private Long id;
    private Long analyseId;
    private Long biologisteId;
    private String nomBiologiste;
    private Double valeurHemoglobine;
    private Double valeurHematocrite;
    private Double nombreGlobulesRouges;
    private Double nombreGlobulesBlancs;
    private Double nombrePlaquettes;
    private String groupeSanguinConfirme;
    private String observations;
    private Boolean valide;
    private LocalDateTime dateResultat;
}
