package com.bloodflow.medical.dashboard.controller;

import com.bloodflow.medical.dashboard.dto.*;
import com.bloodflow.medical.dashboard.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/donneur/{id}")
    public DonneurDashboardDTO getDonneur(@PathVariable Long id) {
        return dashboardService.getDonneurDashboard(id);
    }

    @GetMapping("/patient/{id}")
    public PatientDashboardDTO getPatient(@PathVariable Long id) {
        return dashboardService.getPatientDashboard(id);
    }

    @GetMapping("/medecin/{id}")
    public MedecinDashboardDTO getMedecin(@PathVariable Long id) {
        return dashboardService.getMedecinDashboard(id);
    }

    @GetMapping("/technicien/{id}")
    public TechnicienDashboardDTO getTechnicien(@PathVariable Long id) {
        return dashboardService.getTechnicienDashboard(id);
    }

    @GetMapping("/biologiste/{id}")
    public BiologisteDashboardDTO getBiologiste(@PathVariable Long id) {
        return dashboardService.getBiologisteDashboard(id);
    }

    @GetMapping("/personnel/{id}")
    public PersonnelDashboardDTO getPersonnel(@PathVariable Long id) {
        return dashboardService.getPersonnelDashboard(id);
    }

    @GetMapping("/livreur/{id}")
    public LivreurDashboardDTO getLivreur(@PathVariable Long id) {
        return dashboardService.getLivreurDashboard(id);
    }

    @GetMapping("/admin")
    public AdminDashboardDTO getAdmin() {
        return dashboardService.getAdminDashboard();
    }

    @GetMapping("/agent")
    public AgentDashboardDTO getAgent() {
        return dashboardService.getAgentDashboard();
    }

    @GetMapping("/hopital")
    public HopitalDashboardDTO getHopital() {
        return dashboardService.getHopitalDashboard();
    }
}