package com.bloodflow.medical.dashboard.service.impl;

import com.bloodflow.medical.dashboard.dto.*;
import com.bloodflow.medical.dashboard.service.DashboardService;
import com.bloodflow.medical.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    private final DonneurRepository donneurRepository;
    private final PatientRepository patientRepository;
    private final CommandeSangRepository commandeRepository;
    private final StockRepository stockRepository;
    private final LivraisonRepository livraisonRepository;
    private final AnalyseSangRepository analyseRepository;

    // Convertit le long retourné par repository.count() vers int pour les DTO qui utilisent int.
    private int toInt(long value) {
        return Math.toIntExact(value);
    }

    // ================= DONNEUR =================
    @Override
    public DonneurDashboardDTO getDonneurDashboard(Long id) {
        DonneurDashboardDTO dto = new DonneurDashboardDTO();

        dto.setTotalDons(donneurRepository.count());
        dto.setEligible(true);
        dto.setProchainRdv("2026-06-01");

        return dto;
    }

    // ================= PATIENT =================
    @Override
    public PatientDashboardDTO getPatientDashboard(Long id) {
        PatientDashboardDTO dto = new PatientDashboardDTO();

        dto.setDemandesEnCours(toInt(commandeRepository.count()));
        dto.setStatutDemande("EN_ATTENTE");
        dto.setGroupeSanguin("A confirmer");
        dto.setSangDisponible(stockRepository.count() > 0);

        return dto;
    }

    // ================= MEDECIN =================
    @Override
    public MedecinDashboardDTO getMedecinDashboard(Long id) {
        MedecinDashboardDTO dto = new MedecinDashboardDTO();

        dto.setValidationsDonneurs(toInt(donneurRepository.count()));
        dto.setPrescriptions(toInt(commandeRepository.count()));
        dto.setPatientsSuivis(toInt(patientRepository.count()));

        return dto;
    }

    // ================= TECHNICIEN =================
    @Override
    public TechnicienDashboardDTO getTechnicienDashboard(Long id) {
        TechnicienDashboardDTO dto = new TechnicienDashboardDTO();

        dto.setAnalysesEnCours(toInt(analyseRepository.count()));
        dto.setPrelevements(0);

        return dto;
    }

    // ================= BIOLOGISTE =================
    @Override
    public BiologisteDashboardDTO getBiologisteDashboard(Long id) {
        BiologisteDashboardDTO dto = new BiologisteDashboardDTO();

        dto.setResultatsInterpretes(0);
        dto.setPochesValidees(toInt(stockRepository.count()));

        return dto;
    }

    // ================= PERSONNEL =================
    @Override
    public PersonnelDashboardDTO getPersonnelDashboard(Long id) {
        PersonnelDashboardDTO dto = new PersonnelDashboardDTO();

        dto.setStockDisponible(toInt(stockRepository.count()));
        dto.setCommandesEnCours(toInt(commandeRepository.count()));

        return dto;
    }

    // ================= LIVREUR =================
    @Override
    public LivreurDashboardDTO getLivreurDashboard(Long id) {
        LivreurDashboardDTO dto = new LivreurDashboardDTO();

        dto.setLivraisons(toInt(livraisonRepository.count()));
        dto.setProblemes(0);

        return dto;
    }

    // ================= ADMIN =================
    @Override
    public AdminDashboardDTO getAdminDashboard() {
        AdminDashboardDTO dto = new AdminDashboardDTO();

        dto.setTotalUtilisateurs(toInt(donneurRepository.count() + patientRepository.count()));
        dto.setSystemLogs(0);
        dto.setAlertes(0);

        return dto;
    }

    // ================= AGENT =================
    @Override
    public AgentDashboardDTO getAgentDashboard() {
        AgentDashboardDTO dto = new AgentDashboardDTO();

        dto.setCampagnes(5); // Statique pour l’instant
        dto.setCollectes(0);
        dto.setVolontaires(toInt(donneurRepository.count()));

        return dto;
    }

    // ================= HOPITAL =================
    @Override
    public HopitalDashboardDTO getHopitalDashboard() {
        HopitalDashboardDTO dto = new HopitalDashboardDTO();

        dto.setCommandes(toInt(commandeRepository.count()));
        dto.setLivraisons(toInt(livraisonRepository.count()));

        return dto;
    }
}
