package com.bloodflow.medical.mapper;
import com.bloodflow.medical.dto.request.CommandeSangRequestDTO;
import com.bloodflow.medical.dto.response.CommandeSangResponseDTO;
import com.bloodflow.medical.entity.CommandeSang;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;
@Component
public class CommandeSangMapper {
    private final ModelMapper modelMapper;
    public CommandeSangMapper(ModelMapper modelMapper) { this.modelMapper = modelMapper; }
    public CommandeSangResponseDTO toResponseDTO(CommandeSang entity) {
        CommandeSangResponseDTO dto = modelMapper.map(entity, CommandeSangResponseDTO.class);
        if (entity.getPrescription() != null) dto.setPrescriptionId(entity.getPrescription().getId());
        return dto;
    }
    public CommandeSang toEntity(CommandeSangRequestDTO dto) { return modelMapper.map(dto, CommandeSang.class); }
}
