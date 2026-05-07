package com.bloodflow.medical.service.impl;

import com.bloodflow.medical.dto.request.DossierMedicalRequestDTO;
import com.bloodflow.medical.dto.response.DossierMedicalResponseDTO;
import com.bloodflow.medical.entity.DossierMedical;
import com.bloodflow.medical.exception.BusinessException;
import com.bloodflow.medical.exception.ResourceNotFoundException;
import com.bloodflow.medical.mapper.DossierMedicalMapper;
import com.bloodflow.medical.repository.DossierMedicalRepository;
import com.bloodflow.medical.repository.PatientRepository;
import com.bloodflow.medical.service.DossierMedicalService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class DossierMedicalServiceImpl implements DossierMedicalService {

    private final DossierMedicalRepository dossierMedicalRepository;
    private final PatientRepository patientRepository;
    private final DossierMedicalMapper mapper;

    @Override
    public DossierMedicalResponseDTO create(DossierMedicalRequestDTO dto) {
        if (dossierMedicalRepository.existsByNumeroDossier(dto.getNumeroDossier())) {
            throw new BusinessException("Un dossier avec le numéro '" + dto.getNumeroDossier() + "' existe déjà.");
        }
        DossierMedical dossier = new DossierMedical();
        dossier.setNumeroDossier(dto.getNumeroDossier());
        dossier.setAntecedentsMedicaux(dto.getAntecedentsMedicaux());
        dossier.setAllergies(dto.getAllergies());
        dossier.setGroupeSanguin(dto.getGroupeSanguin());
        dossier.setNotes(dto.getNotes());
        dossier.setPatient(patientRepository.findById(dto.getPatientId())
                .orElseThrow(() -> new ResourceNotFoundException("Patient non trouvé : " + dto.getPatientId())));
        return mapper.toResponseDTO(dossierMedicalRepository.save(dossier));
    }

    @Override
    @Transactional(readOnly = true)
    public DossierMedicalResponseDTO findById(Long id) {
        return mapper.toResponseDTO(dossierMedicalRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Dossier médical non trouvé : " + id)));
    }

    @Override
    @Transactional(readOnly = true)
    public List<DossierMedicalResponseDTO> findAll() {
        return dossierMedicalRepository.findAll().stream().map(mapper::toResponseDTO).collect(Collectors.toList());
    }

    @Override
    public DossierMedicalResponseDTO update(Long id, DossierMedicalRequestDTO dto) {
        DossierMedical dossier = dossierMedicalRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Dossier médical non trouvé : " + id));
        dossier.setAntecedentsMedicaux(dto.getAntecedentsMedicaux());
        dossier.setAllergies(dto.getAllergies());
        dossier.setGroupeSanguin(dto.getGroupeSanguin());
        dossier.setNotes(dto.getNotes());
        return mapper.toResponseDTO(dossierMedicalRepository.save(dossier));
    }

    @Override
    public void delete(Long id) {
        if (!dossierMedicalRepository.existsById(id)) throw new ResourceNotFoundException("Dossier médical non trouvé : " + id);
        dossierMedicalRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DossierMedicalResponseDTO> findByPatient(Long patientId) {
        return dossierMedicalRepository.findByPatientId(patientId).stream().map(mapper::toResponseDTO).collect(Collectors.toList());
    }
}
