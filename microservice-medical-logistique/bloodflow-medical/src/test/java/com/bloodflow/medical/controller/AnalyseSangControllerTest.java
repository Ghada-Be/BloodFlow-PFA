package com.bloodflow.medical.controller;

import com.bloodflow.medical.dto.request.AnalyseSangRequestDTO;
import com.bloodflow.medical.dto.response.AnalyseSangResponseDTO;
import com.bloodflow.medical.entity.EtatAnalyse;
import com.bloodflow.medical.service.AnalyseSangService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import java.util.List;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AnalyseSangController.class)
class AnalyseSangControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @MockBean private AnalyseSangService service;

    @Test
    @WithMockUser(roles = "MEDECIN")
    void getAll_devraitRetourner200() throws Exception {
        AnalyseSangResponseDTO dto = new AnalyseSangResponseDTO();
        dto.setId(1L); dto.setTypeAnalyse("NFS"); dto.setEtat(EtatAnalyse.EN_ATTENTE);
        when(service.findAll()).thenReturn(List.of(dto));
        mockMvc.perform(get("/api/analyses-sang"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].typeAnalyse").value("NFS"));
    }

    @Test
    @WithMockUser(roles = "TECHNICIEN")
    void create_devraitRetourner201() throws Exception {
        AnalyseSangRequestDTO request = new AnalyseSangRequestDTO();
        request.setTypeAnalyse("NFS"); request.setDossierMedicalId(1L);
        AnalyseSangResponseDTO response = new AnalyseSangResponseDTO();
        response.setId(1L); response.setTypeAnalyse("NFS");
        when(service.create(any())).thenReturn(response);
        mockMvc.perform(post("/api/analyses-sang")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void getAll_sansAuthentification_retourne403() throws Exception {
        mockMvc.perform(get("/api/analyses-sang"))
                .andExpect(status().isForbidden());
    }
}
