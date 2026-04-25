package com.bloodflow.medical.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.List;

@Entity
@Table(name = "techniciens_laboratoire")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class TechnicienLaboratoire extends Utilisateur {

    @Column(name = "laboratoire")
    private String laboratoire;

    @Column(name = "certification")
    private String certification;

    @OneToMany(mappedBy = "technicien", cascade = CascadeType.ALL)
    private List<AnalyseSang> analyses;
}
