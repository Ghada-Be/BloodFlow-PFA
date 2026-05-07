package com.bloodflow.medical.dto.response;
import lombok.Data;
import java.time.LocalDateTime;
@Data
public class DossierMedicalResponseDTO {
    private Long id;
    private String numeroDossier;
    private Long patientId;
    private String nomPatient;
    private String prenomPatient;
    private String antecedentsMedicaux;
    private String allergies;
    private String groupeSanguin;
    private String notes;
    private LocalDateTime dateCreation;
    private LocalDateTime dateModification;
}
