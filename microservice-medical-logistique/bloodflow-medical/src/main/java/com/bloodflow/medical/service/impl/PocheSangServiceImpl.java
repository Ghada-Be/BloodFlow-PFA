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

    @Override
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

    @Override @Transactional(readOnly = true)
    public PocheSangResponseDTO findById(Long id) {
        return mapper.toResponseDTO(pocheSangRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Poche de sang non trouvée : " + id)));
    }

    @Override @Transactional(readOnly = true)
    public List<PocheSangResponseDTO> findAll() {
        return pocheSangRepository.findAll().stream().map(mapper::toResponseDTO).collect(Collectors.toList());
    }

    @Override
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

    @Override
    public void delete(Long id) {
        if (!pocheSangRepository.existsById(id)) throw new ResourceNotFoundException("Poche de sang non trouvée : " + id);
        pocheSangRepository.deleteById(id);
    }

    @Override @Transactional(readOnly = true)
    public List<PocheSangResponseDTO> findByStatut(StatutPoche statut) {
        return pocheSangRepository.findByStatut(statut).stream().map(mapper::toResponseDTO).collect(Collectors.toList());
    }

    @Override @Transactional(readOnly = true)
    public List<PocheSangResponseDTO> findDisponiblesByGroupe(String groupeSanguin) {
        return pocheSangRepository.findByGroupeSanguinAndStatut(groupeSanguin, StatutPoche.DISPONIBLE)
                .stream().map(mapper::toResponseDTO).collect(Collectors.toList());
    }
}
