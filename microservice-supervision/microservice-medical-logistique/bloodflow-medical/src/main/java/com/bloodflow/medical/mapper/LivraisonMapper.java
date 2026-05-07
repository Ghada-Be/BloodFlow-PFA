package com.bloodflow.medical.mapper;
import com.bloodflow.medical.dto.request.LivraisonRequestDTO;
import com.bloodflow.medical.dto.response.LivraisonResponseDTO;
import com.bloodflow.medical.entity.Livraison;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;
@Component
public class LivraisonMapper {
    private final ModelMapper modelMapper;
    public LivraisonMapper(ModelMapper modelMapper) { this.modelMapper = modelMapper; }
    public LivraisonResponseDTO toResponseDTO(Livraison entity) {
        LivraisonResponseDTO dto = modelMapper.map(entity, LivraisonResponseDTO.class);
        if (entity.getCommande() != null) {
            dto.setCommandeId(entity.getCommande().getId());
            dto.setNumeroCommande(entity.getCommande().getNumeroCommande());
        }
        if (entity.getLivreur() != null) {
            dto.setLivreurId(entity.getLivreur().getId());
            dto.setNomLivreur(entity.getLivreur().getNom() + " " + entity.getLivreur().getPrenom());
        }
        return dto;
    }
    public Livraison toEntity(LivraisonRequestDTO dto) { return modelMapper.map(dto, Livraison.class); }
}
