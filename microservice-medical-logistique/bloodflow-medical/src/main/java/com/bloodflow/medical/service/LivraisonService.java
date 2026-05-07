package com.bloodflow.medical.service;
import com.bloodflow.medical.dto.request.LivraisonRequestDTO;
import com.bloodflow.medical.dto.response.LivraisonResponseDTO;
import java.util.List;
public interface LivraisonService {
    LivraisonResponseDTO create(LivraisonRequestDTO dto);
    LivraisonResponseDTO findById(Long id);
    List<LivraisonResponseDTO> findAll();
    LivraisonResponseDTO update(Long id, LivraisonRequestDTO dto);
    void delete(Long id);
    List<LivraisonResponseDTO> findByStatut(String statut);
}
