package com.bloodflow.medical.dashboard.service;

import com.bloodflow.medical.dashboard.dto.*;

public interface DashboardService {

    DonneurDashboardDTO getDonneurDashboard(Long id);
    PatientDashboardDTO getPatientDashboard(Long id);
    MedecinDashboardDTO getMedecinDashboard(Long id);
    TechnicienDashboardDTO getTechnicienDashboard(Long id);
    BiologisteDashboardDTO getBiologisteDashboard(Long id);
    PersonnelDashboardDTO getPersonnelDashboard(Long id);
    LivreurDashboardDTO getLivreurDashboard(Long id);
    AdminDashboardDTO getAdminDashboard();
    AgentDashboardDTO getAgentDashboard();
    HopitalDashboardDTO getHopitalDashboard();
}