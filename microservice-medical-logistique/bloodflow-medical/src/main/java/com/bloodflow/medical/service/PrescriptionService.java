package com.bloodflow.medical.service;
import com.bloodflow.medical.dto.request.PrescriptionRequestDTO;
import com.bloodflow.medical.dto.response.PrescriptionResponseDTO;
import java.util.List;
public interface PrescriptionService {
    PrescriptionResponseDTO create(PrescriptionRequestDTO dto);
    PrescriptionResponseDTO findById(Long id);
    List<PrescriptionResponseDTO> findAll();
    PrescriptionResponseDTO update(Long id, PrescriptionRequestDTO dto);
    void delete(Long id);
    List<PrescriptionResponseDTO> findByMedecin(Long medecinId);
    List<PrescriptionResponseDTO> findUrgentes();
}
