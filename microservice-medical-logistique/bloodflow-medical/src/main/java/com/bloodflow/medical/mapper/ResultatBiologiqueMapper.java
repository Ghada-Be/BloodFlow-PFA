package com.bloodflow.medical.mapper;
import com.bloodflow.medical.dto.request.ResultatBiologiqueRequestDTO;
import com.bloodflow.medical.dto.response.ResultatBiologiqueResponseDTO;
import com.bloodflow.medical.entity.ResultatBiologique;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;
@Component
public class ResultatBiologiqueMapper {
    private final ModelMapper modelMapper;
    public ResultatBiologiqueMapper(ModelMapper modelMapper) { this.modelMapper = modelMapper; }
    public ResultatBiologiqueResponseDTO toResponseDTO(ResultatBiologique entity) {
        ResultatBiologiqueResponseDTO dto = modelMapper.map(entity, ResultatBiologiqueResponseDTO.class);
        if (entity.getAnalyse() != null) dto.setAnalyseId(entity.getAnalyse().getId());
        if (entity.getBiologiste() != null) {
            dto.setBiologisteId(entity.getBiologiste().getId());
            dto.setNomBiologiste(entity.getBiologiste().getNom() + " " + entity.getBiologiste().getPrenom());
        }
        return dto;
    }
    public ResultatBiologique toEntity(ResultatBiologiqueRequestDTO dto) { return modelMapper.map(dto, ResultatBiologique.class); }
}
