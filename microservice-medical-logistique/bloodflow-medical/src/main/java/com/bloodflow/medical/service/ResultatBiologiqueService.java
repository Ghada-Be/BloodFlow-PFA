package com.bloodflow.medical.service;
import com.bloodflow.medical.dto.request.ResultatBiologiqueRequestDTO;
import com.bloodflow.medical.dto.response.ResultatBiologiqueResponseDTO;
import java.util.List;
public interface ResultatBiologiqueService {
    ResultatBiologiqueResponseDTO create(ResultatBiologiqueRequestDTO dto);
    ResultatBiologiqueResponseDTO findById(Long id);
    List<ResultatBiologiqueResponseDTO> findAll();
    ResultatBiologiqueResponseDTO update(Long id, ResultatBiologiqueRequestDTO dto);
    void delete(Long id);
    ResultatBiologiqueResponseDTO findByAnalyse(Long analyseId);
}
