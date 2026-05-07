package com.bloodflow.medical.service;
import com.bloodflow.medical.dto.request.StockRequestDTO;
import com.bloodflow.medical.dto.response.StockResponseDTO;
import java.util.List;
public interface StockService {
    StockResponseDTO create(StockRequestDTO dto);
    StockResponseDTO findById(Long id);
    List<StockResponseDTO> findAll();
    StockResponseDTO update(Long id, StockRequestDTO dto);
    void delete(Long id);
    List<StockResponseDTO> findByCentre(String centreSang);
    List<StockResponseDTO> findStocksEnAlerte();
}
