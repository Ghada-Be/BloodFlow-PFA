package com.bloodflow.medical.service;
import com.bloodflow.medical.dto.request.AnalyseSangRequestDTO;
import com.bloodflow.medical.dto.response.AnalyseSangResponseDTO;
import com.bloodflow.medical.entity.AnalyseSang;
import com.bloodflow.medical.entity.DossierMedical;
import com.bloodflow.medical.entity.EtatAnalyse;
import com.bloodflow.medical.exception.BusinessException;
import com.bloodflow.medical.exception.ResourceNotFoundException;
import com.bloodflow.medical.mapper.AnalyseSangMapper;
import com.bloodflow.medical.repository.AnalyseSangRepository;
import com.bloodflow.medical.repository.DossierMedicalRepository;
import com.bloodflow.medical.repository.TechnicienLaboratoireRepository;
import com.bloodflow.medical.service.impl.AnalyseSangServiceImpl;
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
class AnalyseSangServiceTest {
    @Mock private AnalyseSangRepository analyseSangRepository;
    @Mock private DossierMedicalRepository dossierMedicalRepository;
    @Mock private TechnicienLaboratoireRepository technicienRepository;
    @Mock private AnalyseSangMapper mapper;
    @InjectMocks private AnalyseSangServiceImpl service;
    private AnalyseSang analyse;
    private AnalyseSangResponseDTO responseDTO;

    @BeforeEach
    void setUp() {
        analyse = new AnalyseSang();
        analyse.setId(1L); analyse.setTypeAnalyse("NFS"); analyse.setEtat(EtatAnalyse.EN_ATTENTE);
        responseDTO = new AnalyseSangResponseDTO();
        responseDTO.setId(1L); responseDTO.setTypeAnalyse("NFS"); responseDTO.setEtat(EtatAnalyse.EN_ATTENTE);
    }

    @Test
    void findAll_devraitRetournerListe() {
        when(analyseSangRepository.findAll()).thenReturn(List.of(analyse));
        when(mapper.toResponseDTO(analyse)).thenReturn(responseDTO);
        assertThat(service.findAll()).hasSize(1).first().extracting("typeAnalyse").isEqualTo("NFS");
    }

    @Test
    void findById_nonTrouve_leveException() {
        when(analyseSangRepository.findById(99L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.findById(99L)).isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void create_referenceExistante_leveException() {
        AnalyseSangRequestDTO dto = new AnalyseSangRequestDTO();
        dto.setReference("REF-001"); dto.setTypeAnalyse("NFS"); dto.setDossierMedicalId(1L);
        when(analyseSangRepository.existsByReference("REF-001")).thenReturn(true);
        assertThatThrownBy(() -> service.create(dto)).isInstanceOf(BusinessException.class);
    }

    @Test
    void create_devraitCreer() {
        AnalyseSangRequestDTO dto = new AnalyseSangRequestDTO();
        dto.setTypeAnalyse("NFS"); dto.setDossierMedicalId(1L);
        DossierMedical dossier = new DossierMedical(); dossier.setId(1L);
        when(analyseSangRepository.existsByReference(any())).thenReturn(false);
        when(dossierMedicalRepository.findById(1L)).thenReturn(Optional.of(dossier));
        when(analyseSangRepository.save(any())).thenReturn(analyse);
        when(mapper.toResponseDTO(analyse)).thenReturn(responseDTO);
        assertThat(service.create(dto).getTypeAnalyse()).isEqualTo("NFS");
    }

    @Test
    void delete_devraitSupprimer() {
        when(analyseSangRepository.existsById(1L)).thenReturn(true);
        service.delete(1L);
        verify(analyseSangRepository).deleteById(1L);
    }
}
