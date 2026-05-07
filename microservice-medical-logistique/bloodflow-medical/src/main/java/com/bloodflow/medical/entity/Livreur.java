package com.bloodflow.medical.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.List;

@Entity
@Table(name = "livreurs")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Livreur extends Utilisateur {

    @Column(name = "vehicule")
    private String vehicule;

    @Column(name = "zone_livraison")
    private String zoneLivraison;

    @OneToMany(mappedBy = "livreur", cascade = CascadeType.ALL)
    private List<Livraison> livraisons;
}
