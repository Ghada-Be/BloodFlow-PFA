package com.bloodflow.medical.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "donneurs")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Donneur extends Utilisateur {

    @Column(name = "groupe_sanguin")
    private String groupeSanguin;

    @Column(name = "date_dernier_don")
    private LocalDate dateDernierDon;

    @Column(name = "nombre_dons")
    private Integer nombreDons = 0;

    @Column(name = "eligible_don")
    private Boolean eligibleDon = true;
}
