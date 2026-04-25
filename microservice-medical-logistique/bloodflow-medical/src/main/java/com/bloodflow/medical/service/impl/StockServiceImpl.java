package com.bloodflow.medical.service.impl;
import com.bloodflow.medical.dto.request.StockRequestDTO;
import com.bloodflow.medical.dto.response.StockResponseDTO;
import com.bloodflow.medical.entity.Stock;
import com.bloodflow.medical.exception.ResourceNotFoundException;
import com.bloodflow.medical.mapper.StockMapper;
import com.bloodflow.medical.repository.StockRepository;
import com.bloodflow.medical.service.StockService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;
@Service
@RequiredArgsConstructor
@Transactional
public class StockServiceImpl implements StockService {
    private final StockRepository stockRepository;
    private final StockMapper mapper;

    @Override
    public StockResponseDTO create(StockRequestDTO dto) {
        Stock stock = new Stock();
        stock.setCentreSang(dto.getCentreSang());
        stock.setGroupeSanguin(dto.getGroupeSanguin());
        stock.setTypeProduit(dto.getTypeProduit());
        stock.setQuantiteDisponible(dto.getQuantiteDisponible() != null ? dto.getQuantiteDisponible() : 0);
        stock.setSeuilAlerte(dto.getSeuilAlerte() != null ? dto.getSeuilAlerte() : 5);
        return mapper.toResponseDTO(stockRepository.save(stock));
    }

    @Override @Transactional(readOnly = true)
    public StockResponseDTO findById(Long id) {
        return mapper.toResponseDTO(stockRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Stock non trouvé : " + id)));
    }

    @Override @Transactional(readOnly = true)
    public List<StockResponseDTO> findAll() {
        return stockRepository.findAll().stream().map(mapper::toResponseDTO).collect(Collectors.toList());
    }

    @Override
    public StockResponseDTO update(Long id, StockRequestDTO dto) {
        Stock stock = stockRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Stock non trouvé : " + id));
        stock.setQuantiteDisponible(dto.getQuantiteDisponible());
        stock.setSeuilAlerte(dto.getSeuilAlerte());
        stock.setCentreSang(dto.getCentreSang());
        stock.setGroupeSanguin(dto.getGroupeSanguin());
        stock.setTypeProduit(dto.getTypeProduit());
        return mapper.toResponseDTO(stockRepository.save(stock));
    }

    @Override
    public void delete(Long id) {
        if (!stockRepository.existsById(id)) throw new ResourceNotFoundException("Stock non trouvé : " + id);
        stockRepository.deleteById(id);
    }

    @Override @Transactional(readOnly = true)
    public List<StockResponseDTO> findByCentre(String centreSang) {
        return stockRepository.findByCentreSang(centreSang).stream().map(mapper::toResponseDTO).collect(Collectors.toList());
    }

    @Override @Transactional(readOnly = true)
    public List<StockResponseDTO> findStocksEnAlerte() {
        return stockRepository.findStocksBelowThreshold().stream().map(mapper::toResponseDTO).collect(Collectors.toList());
    }
}
