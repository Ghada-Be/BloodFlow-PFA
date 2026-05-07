package com.bloodflow.medical.service;
import com.bloodflow.medical.dto.request.CommandeSangRequestDTO;
import com.bloodflow.medical.dto.response.CommandeSangResponseDTO;
import java.util.List;
public interface CommandeSangService {
    CommandeSangResponseDTO create(CommandeSangRequestDTO dto);
    CommandeSangResponseDTO findById(Long id);
    List<CommandeSangResponseDTO> findAll();
    CommandeSangResponseDTO update(Long id, CommandeSangRequestDTO dto);
    void delete(Long id);
    List<CommandeSangResponseDTO> findByStatut(String statut);
}
