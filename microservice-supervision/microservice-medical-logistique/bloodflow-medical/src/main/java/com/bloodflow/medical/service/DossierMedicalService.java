package com.bloodflow.medical.service;
import com.bloodflow.medical.dto.request.DossierMedicalRequestDTO;
import com.bloodflow.medical.dto.response.DossierMedicalResponseDTO;
import java.util.List;
public interface DossierMedicalService {
    DossierMedicalResponseDTO create(DossierMedicalRequestDTO dto);
    DossierMedicalResponseDTO findById(Long id);
    List<DossierMedicalResponseDTO> findAll();
    DossierMedicalResponseDTO update(Long id, DossierMedicalRequestDTO dto);
    void delete(Long id);
    List<DossierMedicalResponseDTO> findByPatient(Long patientId);
}
