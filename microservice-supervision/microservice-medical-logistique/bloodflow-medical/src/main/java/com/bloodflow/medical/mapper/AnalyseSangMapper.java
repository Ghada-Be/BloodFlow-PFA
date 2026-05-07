package com.bloodflow.medical.mapper;
import com.bloodflow.medical.dto.request.AnalyseSangRequestDTO;
import com.bloodflow.medical.dto.response.AnalyseSangResponseDTO;
import com.bloodflow.medical.entity.AnalyseSang;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;
@Component
public class AnalyseSangMapper {
    private final ModelMapper modelMapper;
    public AnalyseSangMapper(ModelMapper modelMapper) { this.modelMapper = modelMapper; }
    public AnalyseSangResponseDTO toResponseDTO(AnalyseSang entity) {
        AnalyseSangResponseDTO dto = modelMapper.map(entity, AnalyseSangResponseDTO.class);
        if (entity.getDossierMedical() != null) dto.setDossierMedicalId(entity.getDossierMedical().getId());
        if (entity.getTechnicien() != null) {
            dto.setTechnicienId(entity.getTechnicien().getId());
            dto.setNomTechnicien(entity.getTechnicien().getNom() + " " + entity.getTechnicien().getPrenom());
        }
        return dto;
    }
    public AnalyseSang toEntity(AnalyseSangRequestDTO dto) { return modelMapper.map(dto, AnalyseSang.class); }
}
