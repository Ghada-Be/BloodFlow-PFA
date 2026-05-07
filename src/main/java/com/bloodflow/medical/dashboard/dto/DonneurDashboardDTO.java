package com.bloodflow.medical.dashboard.dto;

import lombok.Data;

@Data
public class DonneurDashboardDTO {

    private long totalDons;
    private boolean eligible;
    private String prochainRdv;
}