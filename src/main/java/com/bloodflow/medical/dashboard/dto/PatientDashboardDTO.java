package com.bloodflow.medical.dashboard.dto;

import lombok.Data;

@Data
public class PatientDashboardDTO {
    private int demandesEnCours;
    private String statutDemande;
    private String groupeSanguin;
    private boolean sangDisponible;
}