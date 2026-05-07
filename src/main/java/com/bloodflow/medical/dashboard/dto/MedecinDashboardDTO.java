package com.bloodflow.medical.dashboard.dto;

import lombok.Data;

@Data
public class MedecinDashboardDTO {
    private int validationsDonneurs;
    private int prescriptions;
    private int patientsSuivis;
}