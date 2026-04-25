package com.bloodflow.medical.controller;

import com.bloodflow.medical.dto.request.CommandeSangRequestDTO;
import com.bloodflow.medical.dto.response.CommandeSangResponseDTO;
import com.bloodflow.medical.service.CommandeSangService;
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

@WebMvcTest(CommandeSangController.class)
class CommandeSangControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @MockBean private CommandeSangService service;

    @Test
    @WithMockUser(roles = "MEDECIN")
    void getAll_retourne200() throws Exception {
        CommandeSangResponseDTO dto = new CommandeSangResponseDTO();
        dto.setId(1L); dto.setGroupeSanguin("A+"); dto.setStatut("EN_ATTENTE");
        when(service.findAll()).thenReturn(List.of(dto));
        mockMvc.perform(get("/api/commandes-sang"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].groupeSanguin").value("A+"));
    }

    @Test
    @WithMockUser(roles = "PERSONNEL_CENTRE")
    void create_retourne201() throws Exception {
        CommandeSangRequestDTO request = new CommandeSangRequestDTO();
        request.setGroupeSanguin("A+"); request.setTypeProduit("Sang total"); request.setQuantite(2);
        CommandeSangResponseDTO response = new CommandeSangResponseDTO();
        response.setId(1L); response.setGroupeSanguin("A+");
        when(service.create(any())).thenReturn(response);
        mockMvc.perform(post("/api/commandes-sang")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
    }
}
