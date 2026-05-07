package com.bloodflow.medical.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "personnel_centre_sang")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class PersonnelCentreSang extends Utilisateur {

    @Column(name = "centre_sang")
    private String centreSang;

    @Column(name = "poste")
    private String poste;
}
