package com.bloodflow.medical.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "stocks")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Stock {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "centre_sang", nullable = false)
    private String centreSang;

    @Column(name = "groupe_sanguin", nullable = false)
    private String groupeSanguin;

    @Column(name = "type_produit", nullable = false)
    private String typeProduit;

    @Column(name = "quantite_disponible")
    private Integer quantiteDisponible = 0;

    @Column(name = "seuil_alerte")
    private Integer seuilAlerte = 5;

    @Column(name = "date_mise_a_jour")
    private LocalDateTime dateMiseAJour;

    @OneToMany(mappedBy = "stock", cascade = CascadeType.ALL)
    private List<PocheSang> poches;

    @PrePersist
    protected void onCreate() {
        dateMiseAJour = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        dateMiseAJour = LocalDateTime.now();
    }
}
