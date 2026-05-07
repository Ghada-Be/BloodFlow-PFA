package com.bloodflow.medical.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "resultats_biologiques")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResultatBiologique {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "analyse_id", nullable = false)
    private AnalyseSang analyse;

    @ManyToOne
    @JoinColumn(name = "biologiste_id")
    private Biologiste biologiste;

    @Column(name = "valeur_hemoglobine")
    private Double valeurHemoglobine;

    @Column(name = "valeur_hematocrite")
    private Double valeurHematocrite;

    @Column(name = "nombre_globules_rouges")
    private Double nombreGlobulesRouges;

    @Column(name = "nombre_globules_blancs")
    private Double nombreGlobulesBlancs;

    @Column(name = "nombre_plaquettes")
    private Double nombrePlaquettes;

    @Column(name = "groupe_sanguin_confirme")
    private String groupeSanguinConfirme;

    @Column(name = "observations", columnDefinition = "TEXT")
    private String observations;

    @Column(name = "valide")
    private Boolean valide = false;

    @Column(name = "date_resultat")
    private LocalDateTime dateResultat;

    @PrePersist
    protected void onCreate() {
        dateResultat = LocalDateTime.now();
    }
}
