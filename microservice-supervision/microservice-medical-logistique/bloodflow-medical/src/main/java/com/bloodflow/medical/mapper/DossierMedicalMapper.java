package com.bloodflow.medical.mapper;
import com.bloodflow.medical.dto.request.DossierMedicalRequestDTO;
import com.bloodflow.medical.dto.response.DossierMedicalResponseDTO;
import com.bloodflow.medical.entity.DossierMedical;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;
@Component
public class DossierMedicalMapper {
    private final ModelMapper modelMapper;
    public DossierMedicalMapper(ModelMapper modelMapper) { this.modelMapper = modelMapper; }
    public DossierMedicalResponseDTO toResponseDTO(DossierMedical entity) {
        DossierMedicalResponseDTO dto = modelMapper.map(entity, DossierMedicalResponseDTO.class);
        if (entity.getPatient() != null) {
            dto.setPatientId(entity.getPatient().getId());
            dto.setNomPatient(entity.getPatient().getNom());
            dto.setPrenomPatient(entity.getPatient().getPrenom());
        }
        return dto;
    }
    public DossierMedical toEntity(DossierMedicalRequestDTO dto) { return modelMapper.map(dto, DossierMedical.class); }
}
