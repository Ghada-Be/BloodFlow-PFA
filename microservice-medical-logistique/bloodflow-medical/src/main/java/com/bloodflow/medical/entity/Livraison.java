package com.bloodflow.medical.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "livraisons")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Livraison {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "numero_livraison", unique = true)
    private String numeroLivraison;

    @ManyToOne
    @JoinColumn(name = "commande_id", nullable = false)
    private CommandeSang commande;

    @ManyToOne
    @JoinColumn(name = "livreur_id")
    private Livreur livreur;

    @Column(name = "adresse_livraison", nullable = false)
    private String adresseLivraison;

    @Column(name = "statut", nullable = false)
    private String statut = "EN_ATTENTE";

    @Column(name = "temperature_transport")
    private Double temperatureTransport;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    @Column(name = "date_depart")
    private LocalDateTime dateDepart;

    @Column(name = "date_livraison")
    private LocalDateTime dateLivraison;

    @Column(name = "date_creation")
    private LocalDateTime dateCreation;

    @PrePersist
    protected void onCreate() {
        dateCreation = LocalDateTime.now();
    }
}
