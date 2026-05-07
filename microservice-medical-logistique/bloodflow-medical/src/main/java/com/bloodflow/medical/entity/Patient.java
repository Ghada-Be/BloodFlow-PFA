package com.bloodflow.medical.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "patients")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Patient extends Utilisateur {

    @Column(name = "date_naissance")
    private LocalDate dateNaissance;

    @Column(name = "groupe_sanguin")
    private String groupeSanguin;

    private String adresse;

    @Column(name = "numero_securite_sociale", unique = true)
    private String numeroSecuriteSociale;

    @OneToMany(mappedBy = "patient", cascade = CascadeType.ALL)
    private List<DossierMedical> dossiersMedicaux;
}
