package com.bloodflow.medical.service.impl;

import com.bloodflow.medical.dto.request.PocheSangRequestDTO;
import com.bloodflow.medical.dto.response.PocheSangResponseDTO;
import com.bloodflow.medical.entity.PocheSang;
import com.bloodflow.medical.entity.StatutPoche;
import com.bloodflow.medical.exception.BusinessException;
import com.bloodflow.medical.exception.ResourceNotFoundException;
import com.bloodflow.medical.mapper.PocheSangMapper;
import com.bloodflow.medical.repository.PocheSangRepository;
import com.bloodflow.medical.repository.StockRepository;
import com.bloodflow.medical.service.PocheSangService;
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
public class PocheSangServiceImpl implements PocheSangService {

    private final PocheSangRepository pocheSangRepository;
    private final StockRepository stockRepository;
    private final PocheSangMapper mapper;

    /**
     * CACHE - @CacheEvict : vide tout le cache "poches-sang" après création
     * Nécessaire car la liste des poches disponibles a changé.
     */
    @Override
    @CacheEvict(value = "poches-sang", allEntries = true)
    public PocheSangResponseDTO create(PocheSangRequestDTO dto) {
        if (pocheSangRepository.existsByNumeroPoche(dto.getNumeroPoche())) {
            throw new BusinessException("Une poche avec le numéro '" + dto.getNumeroPoche() + "' existe déjà.");
        }
        PocheSang poche = new PocheSang();
        poche.setNumeroPoche(dto.getNumeroPoche());
        poche.setGroupeSanguin(dto.getGroupeSanguin());
        poche.setTypeProduit(dto.getTypeProduit());
        poche.setVolumeMl(dto.getVolumeMl());
        poche.setDateCollecte(dto.getDateCollecte());
        poche.setDateExpiration(dto.getDateExpiration());
        poche.setCentreCollecte(dto.getCentreCollecte());
        poche.setStatut(StatutPoche.DISPONIBLE);
        if (dto.getStockId() != null) {
            poche.setStock(stockRepository.findById(dto.getStockId())
                    .orElseThrow(() -> new ResourceNotFoundException("Stock non trouvé : " + dto.getStockId())));
        }
        return mapper.toResponseDTO(pocheSangRepository.save(poche));
    }

    /**
     * CACHE - @Cacheable : met en cache la poche par son id
     * key="#id" → chaque poche a sa propre entrée dans le cache
     * Ex: findById(3) → stocké sous la clé 3 dans le cache "poches-sang"
     */
    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "poches-sang", key = "#id")
    public PocheSangResponseDTO findById(Long id) {
        return mapper.toResponseDTO(pocheSangRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Poche de sang non trouvée : " + id)));
    }

    /**
     * CACHE - @Cacheable : met en cache toute la liste des poches
     * Clé fixe "all" pour regrouper cette entrée dans le cache "poches-sang"
     */
    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "poches-sang", key = "'all'")
    public List<PocheSangResponseDTO> findAll() {
        return pocheSangRepository.findAll().stream().map(mapper::toResponseDTO).collect(Collectors.toList());
    }

    /**
     * CACHE - @CacheEvict : vide le cache après mise à jour
     * La poche modifiée (et les listes qui la contiennent) ne sont plus valides.
     */
    @Override
    @CacheEvict(value = "poches-sang", allEntries = true)
    public PocheSangResponseDTO update(Long id, PocheSangRequestDTO dto) {
        PocheSang poche = pocheSangRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Poche de sang non trouvée : " + id));
        poche.setGroupeSanguin(dto.getGroupeSanguin());
        poche.setTypeProduit(dto.getTypeProduit());
        poche.setVolumeMl(dto.getVolumeMl());
        poche.setDateCollecte(dto.getDateCollecte());
        poche.setDateExpiration(dto.getDateExpiration());
        poche.setCentreCollecte(dto.getCentreCollecte());
        return mapper.toResponseDTO(pocheSangRepository.save(poche));
    }

    /**
     * CACHE - @CacheEvict : vide le cache après suppression
     */
    @Override
    @CacheEvict(value = "poches-sang", allEntries = true)
    public void delete(Long id) {
        if (!pocheSangRepository.existsById(id)) throw new ResourceNotFoundException("Poche de sang non trouvée : " + id);
        pocheSangRepository.deleteById(id);
    }

    /**
     * CACHE - @Cacheable avec clé dynamique sur le statut
     * Ex: findByStatut(DISPONIBLE) → clé = "statut_DISPONIBLE"
     * Ex: findByStatut(UTILISEE)   → clé = "statut_UTILISEE"
     */
    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "poches-sang", key = "'statut_' + #statut")
    public List<PocheSangResponseDTO> findByStatut(StatutPoche statut) {
        return pocheSangRepository.findByStatut(statut).stream().map(mapper::toResponseDTO).collect(Collectors.toList());
    }

    /**
     * CACHE - @Cacheable avec clé dynamique sur le groupe sanguin
     * Ex: findDisponiblesByGroupe("A+") → clé = "dispo_A+"
     * Très utile car cette recherche est fréquente lors des commandes urgentes.
     */
    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "poches-sang", key = "'dispo_' + #groupeSanguin")
    public List<PocheSangResponseDTO> findDisponiblesByGroupe(String groupeSanguin) {
        return pocheSangRepository.findByGroupeSanguinAndStatut(groupeSanguin, StatutPoche.DISPONIBLE)
                .stream().map(mapper::toResponseDTO).collect(Collectors.toList());
    }
}
