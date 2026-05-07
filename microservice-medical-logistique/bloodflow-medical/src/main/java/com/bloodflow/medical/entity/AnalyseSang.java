package com.bloodflow.medical.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "analyses_sang")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AnalyseSang {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "reference", unique = true)
    private String reference;

    @ManyToOne
    @JoinColumn(name = "dossier_medical_id", nullable = false)
    private DossierMedical dossierMedical;

    @ManyToOne
    @JoinColumn(name = "technicien_id")
    private TechnicienLaboratoire technicien;

    @Column(name = "type_analyse", nullable = false)
    private String typeAnalyse;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "etat", nullable = false)
    private EtatAnalyse etat = EtatAnalyse.EN_ATTENTE;

    @Column(name = "date_demande")
    private LocalDateTime dateDemande;

    @Column(name = "date_realisation")
    private LocalDateTime dateRealisation;

    @OneToOne(mappedBy = "analyse", cascade = CascadeType.ALL)
    private ResultatBiologique resultat;

    @PrePersist
    protected void onCreate() {
        dateDemande = LocalDateTime.now();
    }
}
