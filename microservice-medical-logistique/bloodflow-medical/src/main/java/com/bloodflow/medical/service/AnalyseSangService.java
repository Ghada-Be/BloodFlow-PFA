package com.bloodflow.medical.service;
import com.bloodflow.medical.dto.request.AnalyseSangRequestDTO;
import com.bloodflow.medical.dto.response.AnalyseSangResponseDTO;
import com.bloodflow.medical.entity.EtatAnalyse;
import java.util.List;
public interface AnalyseSangService {
    AnalyseSangResponseDTO create(AnalyseSangRequestDTO dto);
    AnalyseSangResponseDTO findById(Long id);
    List<AnalyseSangResponseDTO> findAll();
    AnalyseSangResponseDTO update(Long id, AnalyseSangRequestDTO dto);
    void delete(Long id);
    List<AnalyseSangResponseDTO> findByEtat(EtatAnalyse etat);
    List<AnalyseSangResponseDTO> findByDossierMedical(Long dossierId);
}
