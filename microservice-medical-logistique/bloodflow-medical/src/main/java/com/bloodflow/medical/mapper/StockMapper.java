package com.bloodflow.medical.mapper;
import com.bloodflow.medical.dto.request.StockRequestDTO;
import com.bloodflow.medical.dto.response.StockResponseDTO;
import com.bloodflow.medical.entity.Stock;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;
@Component
public class StockMapper {
    private final ModelMapper modelMapper;
    public StockMapper(ModelMapper modelMapper) { this.modelMapper = modelMapper; }
    public StockResponseDTO toResponseDTO(Stock entity) {
        StockResponseDTO dto = modelMapper.map(entity, StockResponseDTO.class);
        dto.setEnAlerte(entity.getQuantiteDisponible() != null && entity.getSeuilAlerte() != null
                && entity.getQuantiteDisponible() <= entity.getSeuilAlerte());
        return dto;
    }
    public Stock toEntity(StockRequestDTO dto) { return modelMapper.map(dto, Stock.class); }
}
