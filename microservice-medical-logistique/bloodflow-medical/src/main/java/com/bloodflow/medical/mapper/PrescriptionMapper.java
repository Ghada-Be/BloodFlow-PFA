package com.bloodflow.medical.mapper;
import com.bloodflow.medical.dto.request.PrescriptionRequestDTO;
import com.bloodflow.medical.dto.response.PrescriptionResponseDTO;
import com.bloodflow.medical.entity.Prescription;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;
@Component
public class PrescriptionMapper {
    private final ModelMapper modelMapper;
    public PrescriptionMapper(ModelMapper modelMapper) { this.modelMapper = modelMapper; }
    public PrescriptionResponseDTO toResponseDTO(Prescription entity) {
        PrescriptionResponseDTO dto = modelMapper.map(entity, PrescriptionResponseDTO.class);
        if (entity.getMedecin() != null) {
            dto.setMedecinId(entity.getMedecin().getId());
            dto.setNomMedecin(entity.getMedecin().getNom() + " " + entity.getMedecin().getPrenom());
        }
        if (entity.getDossierMedical() != null) dto.setDossierMedicalId(entity.getDossierMedical().getId());
        return dto;
    }
    public Prescription toEntity(PrescriptionRequestDTO dto) { return modelMapper.map(dto, Prescription.class); }
}
