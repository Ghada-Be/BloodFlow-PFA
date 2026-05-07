package com.bloodflow.medical.service.impl;

import com.bloodflow.medical.dto.request.ResultatBiologiqueRequestDTO;
import com.bloodflow.medical.dto.response.ResultatBiologiqueResponseDTO;
import com.bloodflow.medical.entity.ResultatBiologique;
import com.bloodflow.medical.exception.ResourceNotFoundException;
import com.bloodflow.medical.mapper.ResultatBiologiqueMapper;
import com.bloodflow.medical.repository.AnalyseSangRepository;
import com.bloodflow.medical.repository.BiologisteRepository;
import com.bloodflow.medical.repository.ResultatBiologiqueRepository;
import com.bloodflow.medical.service.ResultatBiologiqueService;
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
public class ResultatBiologiqueServiceImpl implements ResultatBiologiqueService {

    private final ResultatBiologiqueRepository resultatRepository;
    private final AnalyseSangRepository analyseSangRepository;
    private final BiologisteRepository biologisteRepository;
    private final ResultatBiologiqueMapper mapper;

    /**
     * CACHE - @CacheEvict : vide tout le cache "resultats" après création
     */
    @Override
    @CacheEvict(value = "resultats", allEntries = true)
    public ResultatBiologiqueResponseDTO create(ResultatBiologiqueRequestDTO dto) {
        ResultatBiologique resultat = new ResultatBiologique();
        resultat.setValeurHemoglobine(dto.getValeurHemoglobine());
        resultat.setValeurHematocrite(dto.getValeurHematocrite());
        resultat.setNombreGlobulesRouges(dto.getNombreGlobulesRouges());
        resultat.setNombreGlobulesBlancs(dto.getNombreGlobulesBlancs());
        resultat.setNombrePlaquettes(dto.getNombrePlaquettes());
        resultat.setGroupeSanguinConfirme(dto.getGroupeSanguinConfirme());
        resultat.setObservations(dto.getObservations());
        resultat.setValide(dto.getValide() != null ? dto.getValide() : false);
        resultat.setAnalyse(analyseSangRepository.findById(dto.getAnalyseId())
                .orElseThrow(() -> new ResourceNotFoundException("Analyse non trouvée : " + dto.getAnalyseId())));
        if (dto.getBiologisteId() != null) {
            resultat.setBiologiste(biologisteRepository.findById(dto.getBiologisteId())
                    .orElseThrow(() -> new ResourceNotFoundException("Biologiste non trouvé : " + dto.getBiologisteId())));
        }
        return mapper.toResponseDTO(resultatRepository.save(resultat));
    }

    /**
     * CACHE - @Cacheable : met en cache le résultat par son id
     */
    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "resultats", key = "#id")
    public ResultatBiologiqueResponseDTO findById(Long id) {
        return mapper.toResponseDTO(resultatRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Résultat non trouvé : " + id)));
    }

    /**
     * CACHE - @Cacheable : met en cache toute la liste des résultats
     */
    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "resultats", key = "'all'")
    public List<ResultatBiologiqueResponseDTO> findAll() {
        return resultatRepository.findAll().stream().map(mapper::toResponseDTO).collect(Collectors.toList());
    }

    /**
     * CACHE - @CacheEvict : vide le cache après mise à jour
     */
    @Override
    @CacheEvict(value = "resultats", allEntries = true)
    public ResultatBiologiqueResponseDTO update(Long id, ResultatBiologiqueRequestDTO dto) {
        ResultatBiologique resultat = resultatRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Résultat non trouvé : " + id));
        resultat.setValeurHemoglobine(dto.getValeurHemoglobine());
        resultat.setValeurHematocrite(dto.getValeurHematocrite());
        resultat.setNombreGlobulesRouges(dto.getNombreGlobulesRouges());
        resultat.setNombreGlobulesBlancs(dto.getNombreGlobulesBlancs());
        resultat.setNombrePlaquettes(dto.getNombrePlaquettes());
        resultat.setGroupeSanguinConfirme(dto.getGroupeSanguinConfirme());
        resultat.setObservations(dto.getObservations());
        resultat.setValide(dto.getValide());
        return mapper.toResponseDTO(resultatRepository.save(resultat));
    }

    /**
     * CACHE - @CacheEvict : vide le cache après suppression
     */
    @Override
    @CacheEvict(value = "resultats", allEntries = true)
    public void delete(Long id) {
        if (!resultatRepository.existsById(id)) throw new ResourceNotFoundException("Résultat non trouvé : " + id);
        resultatRepository.deleteById(id);
    }

    /**
     * CACHE - @Cacheable avec clé dynamique sur l'id de l'analyse
     * Ex: findByAnalyse(8) → clé = "analyse_8"
     * Le résultat d'une analyse ne change pas souvent, le cache est très efficace ici.
     */
    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "resultats", key = "'analyse_' + #analyseId")
    public ResultatBiologiqueResponseDTO findByAnalyse(Long analyseId) {
        return mapper.toResponseDTO(resultatRepository.findByAnalyseId(analyseId)
                .orElseThrow(() -> new ResourceNotFoundException("Résultat non trouvé pour l'analyse : " + analyseId)));
    }
}
