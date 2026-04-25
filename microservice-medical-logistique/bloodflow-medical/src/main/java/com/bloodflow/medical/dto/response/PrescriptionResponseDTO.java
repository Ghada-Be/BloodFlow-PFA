package com.bloodflow.medical.dto.response;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;
@Data
public class PrescriptionResponseDTO {
    private Long id;
    private Long medecinId;
    private String nomMedecin;
    private Long dossierMedicalId;
    private String typeProduitSanguin;
    private Integer quantite;
    private String groupeSanguinRequis;
    private Boolean urgence;
    private String motif;
    private LocalDate datePrescription;
    private LocalDateTime dateCreation;
}
