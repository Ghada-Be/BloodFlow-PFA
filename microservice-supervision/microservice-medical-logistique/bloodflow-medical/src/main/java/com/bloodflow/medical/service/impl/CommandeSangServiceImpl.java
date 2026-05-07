package com.bloodflow.medical.service.impl;

import com.bloodflow.medical.dto.request.CommandeSangRequestDTO;
import com.bloodflow.medical.dto.response.CommandeSangResponseDTO;
import com.bloodflow.medical.entity.CommandeSang;
import com.bloodflow.medical.exception.BusinessException;
import com.bloodflow.medical.exception.ResourceNotFoundException;
import com.bloodflow.medical.mapper.CommandeSangMapper;
import com.bloodflow.medical.repository.CommandeSangRepository;
import com.bloodflow.medical.repository.PrescriptionRepository;
import com.bloodflow.medical.service.CommandeSangService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class CommandeSangServiceImpl implements CommandeSangService {

    private final CommandeSangRepository commandeSangRepository;
    private final PrescriptionRepository prescriptionRepository;
    private final CommandeSangMapper mapper;

    @Override
    public CommandeSangResponseDTO create(CommandeSangRequestDTO dto) {
        CommandeSang commande = new CommandeSang();
        commande.setNumeroCommande("CMD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        commande.setGroupeSanguin(dto.getGroupeSanguin());
        commande.setTypeProduit(dto.getTypeProduit());
        commande.setQuantite(dto.getQuantite());
        commande.setUrgence(dto.getUrgence() != null ? dto.getUrgence() : false);
        commande.setHopitalDemandeur(dto.getHopitalDemandeur());
        commande.setNotes(dto.getNotes());
        commande.setDateLivraisonSouhaitee(dto.getDateLivraisonSouhaitee());
        commande.setStatut("EN_ATTENTE");
        if (dto.getPrescriptionId() != null) {
            commande.setPrescription(prescriptionRepository.findById(dto.getPrescriptionId())
                    .orElseThrow(() -> new ResourceNotFoundException("Prescription non trouvée : " + dto.getPrescriptionId())));
        }
        return mapper.toResponseDTO(commandeSangRepository.save(commande));
    }

    @Override
    @Transactional(readOnly = true)
    public CommandeSangResponseDTO findById(Long id) {
        return mapper.toResponseDTO(commandeSangRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Commande non trouvée : " + id)));
    }

    @Override
    @Transactional(readOnly = true)
    public List<CommandeSangResponseDTO> findAll() {
        return commandeSangRepository.findAll().stream().map(mapper::toResponseDTO).collect(Collectors.toList());
    }

    @Override
    public CommandeSangResponseDTO update(Long id, CommandeSangRequestDTO dto) {
        CommandeSang commande = commandeSangRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Commande non trouvée : " + id));
        commande.setGroupeSanguin(dto.getGroupeSanguin());
        commande.setTypeProduit(dto.getTypeProduit());
        commande.setQuantite(dto.getQuantite());
        commande.setUrgence(dto.getUrgence());
        commande.setHopitalDemandeur(dto.getHopitalDemandeur());
        commande.setNotes(dto.getNotes());
        commande.setDateLivraisonSouhaitee(dto.getDateLivraisonSouhaitee());
        return mapper.toResponseDTO(commandeSangRepository.save(commande));
    }

    @Override
    public void delete(Long id) {
        if (!commandeSangRepository.existsById(id)) throw new ResourceNotFoundException("Commande non trouvée : " + id);
        commandeSangRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CommandeSangResponseDTO> findByStatut(String statut) {
        return commandeSangRepository.findByStatut(statut).stream().map(mapper::toResponseDTO).collect(Collectors.toList());
    }
}
