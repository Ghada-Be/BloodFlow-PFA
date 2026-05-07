package com.bloodflow.medical.service;
import com.bloodflow.medical.dto.request.PocheSangRequestDTO;
import com.bloodflow.medical.dto.response.PocheSangResponseDTO;
import com.bloodflow.medical.entity.StatutPoche;
import java.util.List;
public interface PocheSangService {
    PocheSangResponseDTO create(PocheSangRequestDTO dto);
    PocheSangResponseDTO findById(Long id);
    List<PocheSangResponseDTO> findAll();
    PocheSangResponseDTO update(Long id, PocheSangRequestDTO dto);
    void delete(Long id);
    List<PocheSangResponseDTO> findByStatut(StatutPoche statut);
    List<PocheSangResponseDTO> findDisponiblesByGroupe(String groupeSanguin);
}
