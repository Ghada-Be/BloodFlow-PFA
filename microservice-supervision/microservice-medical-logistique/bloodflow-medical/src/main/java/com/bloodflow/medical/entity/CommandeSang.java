package com.bloodflow.medical.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "commandes_sang")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommandeSang {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "numero_commande", unique = true, nullable = false)
    private String numeroCommande;

    @ManyToOne
    @JoinColumn(name = "prescription_id")
    private Prescription prescription;

    @Column(name = "groupe_sanguin", nullable = false)
    private String groupeSanguin;

    @Column(name = "type_produit", nullable = false)
    private String typeProduit;

    @Column(name = "quantite", nullable = false)
    private Integer quantite;

    @Column(name = "statut", nullable = false)
    private String statut = "EN_ATTENTE";

    @Column(name = "urgence")
    private Boolean urgence = false;

    @Column(name = "hopital_demandeur")
    private String hopitalDemandeur;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    @Column(name = "date_commande")
    private LocalDateTime dateCommande;

    @Column(name = "date_livraison_souhaitee")
    private LocalDateTime dateLivraisonSouhaitee;

    @PrePersist
    protected void onCreate() {
        dateCommande = LocalDateTime.now();
    }
}
