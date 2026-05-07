package com.bloodflow.medical.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "prescriptions")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Prescription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "medecin_id", nullable = false)
    private Medecin medecin;

    @ManyToOne
    @JoinColumn(name = "dossier_medical_id", nullable = false)
    private DossierMedical dossierMedical;

    @Column(name = "type_produit_sanguin", nullable = false)
    private String typeProduitSanguin;

    @Column(name = "quantite")
    private Integer quantite;

    @Column(name = "groupe_sanguin_requis")
    private String groupeSanguinRequis;

    @Column(name = "urgence")
    private Boolean urgence = false;

    @Column(name = "motif", columnDefinition = "TEXT")
    private String motif;

    @Column(name = "date_prescription")
    private LocalDate datePrescription;

    @Column(name = "date_creation")
    private LocalDateTime dateCreation;

    @PrePersist
    protected void onCreate() {
        dateCreation = LocalDateTime.now();
        if (datePrescription == null) {
            datePrescription = LocalDate.now();
        }
    }
}
