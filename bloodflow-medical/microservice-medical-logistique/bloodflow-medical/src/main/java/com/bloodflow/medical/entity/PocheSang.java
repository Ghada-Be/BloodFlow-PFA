package com.bloodflow.medical.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "poches_sang")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PocheSang {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "numero_poche", unique = true, nullable = false)
    private String numeroPoche;

    @Column(name = "groupe_sanguin", nullable = false)
    private String groupeSanguin;

    @Column(name = "type_produit", nullable = false)
    private String typeProduit;

    @Column(name = "volume_ml")
    private Integer volumeMl;

    @Enumerated(EnumType.STRING)
    @Column(name = "statut", nullable = false)
    private StatutPoche statut = StatutPoche.DISPONIBLE;

    @Column(name = "date_collecte")
    private LocalDate dateCollecte;

    @Column(name = "date_expiration")
    private LocalDate dateExpiration;

    @Column(name = "centre_collecte")
    private String centreCollecte;

    @ManyToOne
    @JoinColumn(name = "stock_id")
    private Stock stock;

    @Column(name = "date_creation")
    private LocalDateTime dateCreation;

    @PrePersist
    protected void onCreate() {
        dateCreation = LocalDateTime.now();
    }
}
