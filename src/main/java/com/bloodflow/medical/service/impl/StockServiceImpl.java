package com.bloodflow.medical.service.impl;

import com.bloodflow.medical.dto.request.StockRequestDTO;
import com.bloodflow.medical.dto.response.StockResponseDTO;
import com.bloodflow.medical.entity.Stock;
import com.bloodflow.medical.exception.ResourceNotFoundException;
import com.bloodflow.medical.mapper.StockMapper;
import com.bloodflow.medical.repository.StockRepository;
import com.bloodflow.medical.service.StockService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
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

    /**
     * CACHE - @CacheEvict sur "stocks"
     * Quand on crée un nouveau stock, on vide tout le cache "stocks"
     * pour que les prochains appels findAll() et findByCentre()
     * récupèrent les données fraîches depuis la base de données.
     * allEntries=true : supprime TOUTES les entrées du cache (pas juste une clé)
     */
    @Override
    @CacheEvict(value = "stocks", allEntries = true)
    public StockResponseDTO create(StockRequestDTO dto) {
        Stock stock = new Stock();
        stock.setCentreSang(dto.getCentreSang());
        stock.setGroupeSanguin(dto.getGroupeSanguin());
        stock.setTypeProduit(dto.getTypeProduit());
        stock.setQuantiteDisponible(dto.getQuantiteDisponible() != null ? dto.getQuantiteDisponible() : 0);
        stock.setSeuilAlerte(dto.getSeuilAlerte() != null ? dto.getSeuilAlerte() : 5);
        return mapper.toResponseDTO(stockRepository.save(stock));
    }

    /**
     * CACHE - @Cacheable sur "stocks"
     * La première fois qu'on appelle findById(5), Spring va en DB et stocke
     * le résultat dans le cache avec la clé = 5.
     * Les prochains appels findById(5) retournent directement depuis le cache
     * sans toucher la base de données.
     * key="#id" : la clé du cache est la valeur du paramètre id
     */
    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "stocks", key = "#id")
    public StockResponseDTO findById(Long id) {
        return mapper.toResponseDTO(stockRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Stock non trouvé : " + id)));
    }

    /**
     * CACHE - @Cacheable sur "stocks" avec clé fixe "all"
     * La liste complète est mise en cache sous la clé "all".
     * Tant que le cache n'est pas invalidé, findAll() ne touche plus la DB.
     */
    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "stocks", key = "'all'")
    public List<StockResponseDTO> findAll() {
        return stockRepository.findAll().stream().map(mapper::toResponseDTO).collect(Collectors.toList());
    }

    /**
     * CACHE - @CacheEvict sur "stocks"
     * Quand on met à jour un stock, on vide tout le cache "stocks"
     * pour éviter de servir des données obsolètes (stale data).
     */
    @Override
    @CacheEvict(value = "stocks", allEntries = true)
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

    /**
     * CACHE - @CacheEvict sur "stocks"
     * Quand on supprime un stock, on vide tout le cache "stocks"
     * pour que la prochaine liste ne contienne plus l'élément supprimé.
     */
    @Override
    @CacheEvict(value = "stocks", allEntries = true)
    public void delete(Long id) {
        if (!stockRepository.existsById(id)) throw new ResourceNotFoundException("Stock non trouvé : " + id);
        stockRepository.deleteById(id);
    }

    /**
     * CACHE - @Cacheable avec clé dynamique basée sur centreSang
     * Ex: findByCentre("Paris") → clé = "centre_Paris"
     * Ex: findByCentre("Lyon")  → clé = "centre_Lyon"
     * Chaque centre a sa propre entrée dans le cache.
     */
    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "stocks", key = "'centre_' + #centreSang")
    public List<StockResponseDTO> findByCentre(String centreSang) {
        return stockRepository.findByCentreSang(centreSang).stream().map(mapper::toResponseDTO).collect(Collectors.toList());
    }

    /**
     * CACHE - @Cacheable avec clé fixe "alerte"
     * La liste des stocks en alerte est souvent consultée.
     * Mise en cache pour éviter une requête SQL à chaque vérification.
     */
    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "stocks", key = "'alerte'")
    public List<StockResponseDTO> findStocksEnAlerte() {
        return stockRepository.findStocksBelowThreshold().stream().map(mapper::toResponseDTO).collect(Collectors.toList());
    }
}
