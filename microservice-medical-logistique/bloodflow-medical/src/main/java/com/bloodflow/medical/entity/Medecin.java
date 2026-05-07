package com.bloodflow.medical.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.List;

@Entity
@Table(name = "medecins")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Medecin extends Utilisateur {

    private String specialite;

    @Column(name = "numero_ordre")
    private String numeroOrdre;

    @OneToMany(mappedBy = "medecin", cascade = CascadeType.ALL)
    private List<Prescription> prescriptions;
}
