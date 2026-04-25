package com.bloodflow.medical.service.impl;
import com.bloodflow.medical.dto.request.PrescriptionRequestDTO;
import com.bloodflow.medical.dto.response.PrescriptionResponseDTO;
import com.bloodflow.medical.entity.Prescription;
import com.bloodflow.medical.exception.ResourceNotFoundException;
import com.bloodflow.medical.mapper.PrescriptionMapper;
import com.bloodflow.medical.repository.DossierMedicalRepository;
import com.bloodflow.medical.repository.MedecinRepository;
import com.bloodflow.medical.repository.PrescriptionRepository;
import com.bloodflow.medical.service.PrescriptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;
@Service
@RequiredArgsConstructor
@Transactional
public class PrescriptionServiceImpl implements PrescriptionService {
    private final PrescriptionRepository prescriptionRepository;
    private final MedecinRepository medecinRepository;
    private final DossierMedicalRepository dossierMedicalRepository;
    private final PrescriptionMapper mapper;

    @Override
    public PrescriptionResponseDTO create(PrescriptionRequestDTO dto) {
        Prescription prescription = new Prescription();
        prescription.setTypeProduitSanguin(dto.getTypeProduitSanguin());
        prescription.setQuantite(dto.getQuantite());
        prescription.setGroupeSanguinRequis(dto.getGroupeSanguinRequis());
        prescription.setUrgence(dto.getUrgence() != null ? dto.getUrgence() : false);
        prescription.setMotif(dto.getMotif());
        prescription.setDatePrescription(dto.getDatePrescription());
        prescription.setMedecin(medecinRepository.findById(dto.getMedecinId())
                .orElseThrow(() -> new ResourceNotFoundException("Médecin non trouvé : " + dto.getMedecinId())));
        prescription.setDossierMedical(dossierMedicalRepository.findById(dto.getDossierMedicalId())
                .orElseThrow(() -> new ResourceNotFoundException("Dossier médical non trouvé : " + dto.getDossierMedicalId())));
        return mapper.toResponseDTO(prescriptionRepository.save(prescription));
    }

    @Override @Transactional(readOnly = true)
    public PrescriptionResponseDTO findById(Long id) {
        return mapper.toResponseDTO(prescriptionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Prescription non trouvée : " + id)));
    }

    @Override @Transactional(readOnly = true)
    public List<PrescriptionResponseDTO> findAll() {
        return prescriptionRepository.findAll().stream().map(mapper::toResponseDTO).collect(Collectors.toList());
    }

    @Override
    public PrescriptionResponseDTO update(Long id, PrescriptionRequestDTO dto) {
        Prescription prescription = prescriptionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Prescription non trouvée : " + id));
        prescription.setTypeProduitSanguin(dto.getTypeProduitSanguin());
        prescription.setQuantite(dto.getQuantite());
        prescription.setGroupeSanguinRequis(dto.getGroupeSanguinRequis());
        prescription.setUrgence(dto.getUrgence());
        prescription.setMotif(dto.getMotif());
        return mapper.toResponseDTO(prescriptionRepository.save(prescription));
    }

    @Override
    public void delete(Long id) {
        if (!prescriptionRepository.existsById(id)) throw new ResourceNotFoundException("Prescription non trouvée : " + id);
        prescriptionRepository.deleteById(id);
    }

    @Override @Transactional(readOnly = true)
    public List<PrescriptionResponseDTO> findByMedecin(Long medecinId) {
        return prescriptionRepository.findByMedecinId(medecinId).stream().map(mapper::toResponseDTO).collect(Collectors.toList());
    }

    @Override @Transactional(readOnly = true)
    public List<PrescriptionResponseDTO> findUrgentes() {
        return prescriptionRepository.findByUrgence(true).stream().map(mapper::toResponseDTO).collect(Collectors.toList());
    }
}
