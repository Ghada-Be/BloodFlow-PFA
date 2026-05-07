package com.bloodflow.medical.service;
import com.bloodflow.medical.dto.request.CommandeSangRequestDTO;
import com.bloodflow.medical.dto.response.CommandeSangResponseDTO;
import com.bloodflow.medical.entity.CommandeSang;
import com.bloodflow.medical.exception.ResourceNotFoundException;
import com.bloodflow.medical.mapper.CommandeSangMapper;
import com.bloodflow.medical.repository.CommandeSangRepository;
import com.bloodflow.medical.repository.PrescriptionRepository;
import com.bloodflow.medical.service.impl.CommandeSangServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.List;
import java.util.Optional;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CommandeSangServiceTest {
    @Mock private CommandeSangRepository commandeSangRepository;
    @Mock private PrescriptionRepository prescriptionRepository;
    @Mock private CommandeSangMapper mapper;
    @InjectMocks private CommandeSangServiceImpl service;
    private CommandeSang commande;
    private CommandeSangResponseDTO responseDTO;

    @BeforeEach
    void setUp() {
        commande = new CommandeSang();
        commande.setId(1L); commande.setGroupeSanguin("A+"); commande.setQuantite(2); commande.setStatut("EN_ATTENTE");
        responseDTO = new CommandeSangResponseDTO();
        responseDTO.setId(1L); responseDTO.setGroupeSanguin("A+");
    }

    @Test
    void findAll_retourneListe() {
        when(commandeSangRepository.findAll()).thenReturn(List.of(commande));
        when(mapper.toResponseDTO(commande)).thenReturn(responseDTO);
        assertThat(service.findAll()).hasSize(1);
    }

    @Test
    void findById_nonTrouve_leveException() {
        when(commandeSangRepository.findById(99L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.findById(99L)).isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void create_avecNumeroAuto() {
        CommandeSangRequestDTO dto = new CommandeSangRequestDTO();
        dto.setGroupeSanguin("A+"); dto.setTypeProduit("Sang total"); dto.setQuantite(2);
        when(commandeSangRepository.save(any())).thenReturn(commande);
        when(mapper.toResponseDTO(commande)).thenReturn(responseDTO);
        assertThat(service.create(dto).getGroupeSanguin()).isEqualTo("A+");
        verify(commandeSangRepository).save(any());
    }

    @Test
    void findByStatut_filtreBien() {
        when(commandeSangRepository.findByStatut("EN_ATTENTE")).thenReturn(List.of(commande));
        when(mapper.toResponseDTO(commande)).thenReturn(responseDTO);
        assertThat(service.findByStatut("EN_ATTENTE")).hasSize(1);
    }
}
