package com.bloodflow.medical.service.impl;

import com.bloodflow.medical.dto.request.AnalyseSangRequestDTO;
import com.bloodflow.medical.dto.response.AnalyseSangResponseDTO;
import com.bloodflow.medical.entity.AnalyseSang;
import com.bloodflow.medical.entity.EtatAnalyse;
import com.bloodflow.medical.exception.BusinessException;
import com.bloodflow.medical.exception.ResourceNotFoundException;
import com.bloodflow.medical.mapper.AnalyseSangMapper;
import com.bloodflow.medical.repository.AnalyseSangRepository;
import com.bloodflow.medical.repository.DossierMedicalRepository;
import com.bloodflow.medical.repository.TechnicienLaboratoireRepository;
import com.bloodflow.medical.service.AnalyseSangService;
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
public class AnalyseSangServiceImpl implements AnalyseSangService {

    private final AnalyseSangRepository analyseSangRepository;
    private final DossierMedicalRepository dossierMedicalRepository;
    private final TechnicienLaboratoireRepository technicienRepository;
    private final AnalyseSangMapper mapper;

    /**
     * CACHE - @CacheEvict : vide tout le cache "analyses" après création
     * Nécessaire pour que findAll() et findByDossierMedical() retournent
     * les données à jour incluant la nouvelle analyse.
     */
    @Override
    @CacheEvict(value = "analyses", allEntries = true)
    public AnalyseSangResponseDTO create(AnalyseSangRequestDTO dto) {
        if (dto.getReference() != null && analyseSangRepository.existsByReference(dto.getReference())) {
            throw new BusinessException("Une analyse avec la référence '" + dto.getReference() + "' existe déjà.");
        }
        AnalyseSang analyse = new AnalyseSang();
        analyse.setTypeAnalyse(dto.getTypeAnalyse());
        analyse.setDescription(dto.getDescription());
        analyse.setReference(dto.getReference());
        analyse.setEtat(EtatAnalyse.EN_ATTENTE);
        analyse.setDossierMedical(dossierMedicalRepository.findById(dto.getDossierMedicalId())
                .orElseThrow(() -> new ResourceNotFoundException("Dossier médical non trouvé : " + dto.getDossierMedicalId())));
        if (dto.getTechnicienId() != null) {
            analyse.setTechnicien(technicienRepository.findById(dto.getTechnicienId())
                    .orElseThrow(() -> new ResourceNotFoundException("Technicien non trouvé : " + dto.getTechnicienId())));
        }
        return mapper.toResponseDTO(analyseSangRepository.save(analyse));
    }

    /**
     * CACHE - @Cacheable : met en cache l'analyse par son id
     * key="#id" → chaque analyse a sa propre entrée dans le cache "analyses"
     */
    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "analyses", key = "#id")
    public AnalyseSangResponseDTO findById(Long id) {
        return mapper.toResponseDTO(analyseSangRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Analyse non trouvée : " + id)));
    }

    /**
     * CACHE - @Cacheable : met en cache toute la liste des analyses
     */
    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "analyses", key = "'all'")
    public List<AnalyseSangResponseDTO> findAll() {
        return analyseSangRepository.findAll().stream().map(mapper::toResponseDTO).collect(Collectors.toList());
    }

    /**
     * CACHE - @CacheEvict : vide le cache après mise à jour
     */
    @Override
    @CacheEvict(value = "analyses", allEntries = true)
    public AnalyseSangResponseDTO update(Long id, AnalyseSangRequestDTO dto) {
        AnalyseSang analyse = analyseSangRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Analyse non trouvée : " + id));
        analyse.setTypeAnalyse(dto.getTypeAnalyse());
        analyse.setDescription(dto.getDescription());
        if (dto.getTechnicienId() != null) {
            analyse.setTechnicien(technicienRepository.findById(dto.getTechnicienId())
                    .orElseThrow(() -> new ResourceNotFoundException("Technicien non trouvé : " + dto.getTechnicienId())));
        }
        return mapper.toResponseDTO(analyseSangRepository.save(analyse));
    }

    /**
     * CACHE - @CacheEvict : vide le cache après suppression
     */
    @Override
    @CacheEvict(value = "analyses", allEntries = true)
    public void delete(Long id) {
        if (!analyseSangRepository.existsById(id)) throw new ResourceNotFoundException("Analyse non trouvée : " + id);
        analyseSangRepository.deleteById(id);
    }

    /**
     * CACHE - @Cacheable avec clé dynamique sur l'état de l'analyse
     * Ex: findByEtat(EN_ATTENTE) → clé = "etat_EN_ATTENTE"
     */
    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "analyses", key = "'etat_' + #etat")
    public List<AnalyseSangResponseDTO> findByEtat(EtatAnalyse etat) {
        return analyseSangRepository.findByEtat(etat).stream().map(mapper::toResponseDTO).collect(Collectors.toList());
    }

    /**
     * CACHE - @Cacheable avec clé dynamique sur l'id du dossier médical
     * Ex: findByDossierMedical(12) → clé = "dossier_12"
     * Très utile quand on consulte souvent les analyses d'un même patient.
     */
    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "analyses", key = "'dossier_' + #dossierId")
    public List<AnalyseSangResponseDTO> findByDossierMedical(Long dossierId) {
        return analyseSangRepository.findByDossierMedicalId(dossierId).stream().map(mapper::toResponseDTO).collect(Collectors.toList());
    }
}
