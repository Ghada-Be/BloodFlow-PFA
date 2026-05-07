package com.bloodflow.medical.service.impl;

import com.bloodflow.medical.dto.request.LivraisonRequestDTO;
import com.bloodflow.medical.dto.response.LivraisonResponseDTO;
import com.bloodflow.medical.entity.Livraison;
import com.bloodflow.medical.exception.ResourceNotFoundException;
import com.bloodflow.medical.mapper.LivraisonMapper;
import com.bloodflow.medical.repository.CommandeSangRepository;
import com.bloodflow.medical.repository.LivraisonRepository;
import com.bloodflow.medical.repository.LivreurRepository;
import com.bloodflow.medical.service.LivraisonService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class LivraisonServiceImpl implements LivraisonService {

    private final LivraisonRepository livraisonRepository;
    private final CommandeSangRepository commandeSangRepository;
    private final LivreurRepository livreurRepository;
    private final LivraisonMapper mapper;

    /**
     * CACHE - @CacheEvict : vide tout le cache "livraisons" après création
     */
    @Override
    @CacheEvict(value = "livraisons", allEntries = true)
    public LivraisonResponseDTO create(LivraisonRequestDTO dto) {
        Livraison livraison = new Livraison();
        livraison.setNumeroLivraison("LIV-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        livraison.setAdresseLivraison(dto.getAdresseLivraison());
        livraison.setTemperatureTransport(dto.getTemperatureTransport());
        livraison.setNotes(dto.getNotes());
        livraison.setDateDepart(dto.getDateDepart());
        livraison.setStatut("EN_ATTENTE");
        livraison.setCommande(commandeSangRepository.findById(dto.getCommandeId())
                .orElseThrow(() -> new ResourceNotFoundException("Commande non trouvée : " + dto.getCommandeId())));
        if (dto.getLivreurId() != null) {
            livraison.setLivreur(livreurRepository.findById(dto.getLivreurId())
                    .orElseThrow(() -> new ResourceNotFoundException("Livreur non trouvé : " + dto.getLivreurId())));
        }
        return mapper.toResponseDTO(livraisonRepository.save(livraison));
    }

    /**
     * CACHE - @Cacheable : met en cache la livraison par son id
     */
    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "livraisons", key = "#id")
    public LivraisonResponseDTO findById(Long id) {
        return mapper.toResponseDTO(livraisonRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Livraison non trouvée : " + id)));
    }

    /**
     * CACHE - @Cacheable : met en cache toute la liste des livraisons
     */
    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "livraisons", key = "'all'")
    public List<LivraisonResponseDTO> findAll() {
        return livraisonRepository.findAll().stream().map(mapper::toResponseDTO).collect(Collectors.toList());
    }

    /**
     * CACHE - @CacheEvict : vide le cache après mise à jour
     */
    @Override
    @CacheEvict(value = "livraisons", allEntries = true)
    public LivraisonResponseDTO update(Long id, LivraisonRequestDTO dto) {
        Livraison livraison = livraisonRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Livraison non trouvée : " + id));
        livraison.setAdresseLivraison(dto.getAdresseLivraison());
        livraison.setTemperatureTransport(dto.getTemperatureTransport());
        livraison.setNotes(dto.getNotes());
        livraison.setDateDepart(dto.getDateDepart());
        return mapper.toResponseDTO(livraisonRepository.save(livraison));
    }

    /**
     * CACHE - @CacheEvict : vide le cache après suppression
     */
    @Override
    @CacheEvict(value = "livraisons", allEntries = true)
    public void delete(Long id) {
        if (!livraisonRepository.existsById(id)) throw new ResourceNotFoundException("Livraison non trouvée : " + id);
        livraisonRepository.deleteById(id);
    }

    /**
     * CACHE - @Cacheable avec clé dynamique sur le statut de la livraison
     * Ex: findByStatut("LIVREE") → clé = "statut_LIVREE"
     */
    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "livraisons", key = "'statut_' + #statut")
    public List<LivraisonResponseDTO> findByStatut(String statut) {
        return livraisonRepository.findByStatut(statut).stream().map(mapper::toResponseDTO).collect(Collectors.toList());
    }
}
