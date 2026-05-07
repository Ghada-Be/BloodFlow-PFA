package com.bloodflow.medical.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.List;

@Entity
@Table(name = "biologistes")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Biologiste extends Utilisateur {

    @Column(name = "laboratoire")
    private String laboratoire;

    @Column(name = "numero_agrement")
    private String numeroAgrement;

    @OneToMany(mappedBy = "biologiste", cascade = CascadeType.ALL)
    private List<ResultatBiologique> resultats;
}
