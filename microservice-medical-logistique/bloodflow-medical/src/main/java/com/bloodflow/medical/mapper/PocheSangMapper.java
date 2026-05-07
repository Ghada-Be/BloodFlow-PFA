package com.bloodflow.medical.mapper;
import com.bloodflow.medical.dto.request.PocheSangRequestDTO;
import com.bloodflow.medical.dto.response.PocheSangResponseDTO;
import com.bloodflow.medical.entity.PocheSang;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;
@Component
public class PocheSangMapper {
    private final ModelMapper modelMapper;
    public PocheSangMapper(ModelMapper modelMapper) { this.modelMapper = modelMapper; }
    public PocheSangResponseDTO toResponseDTO(PocheSang entity) {
        PocheSangResponseDTO dto = modelMapper.map(entity, PocheSangResponseDTO.class);
        if (entity.getStock() != null) dto.setStockId(entity.getStock().getId());
        return dto;
    }
    public PocheSang toEntity(PocheSangRequestDTO dto) { return modelMapper.map(dto, PocheSang.class); }
}
