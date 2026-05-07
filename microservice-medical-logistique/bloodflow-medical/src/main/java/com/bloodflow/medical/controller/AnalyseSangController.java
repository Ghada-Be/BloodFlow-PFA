package com.bloodflow.medical.controller;
import com.bloodflow.medical.dto.request.AnalyseSangRequestDTO;
import com.bloodflow.medical.dto.response.AnalyseSangResponseDTO;
import com.bloodflow.medical.entity.EtatAnalyse;
import com.bloodflow.medical.service.AnalyseSangService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
@RestController
@RequestMapping("/api/analyses-sang")
@RequiredArgsConstructor
@Tag(name = "Analyses de Sang", description = "Gestion des analyses biologiques")
@SecurityRequirement(name = "bearerAuth")
public class AnalyseSangController {
    private final AnalyseSangService service;
    @PostMapping
    @Operation(summary = "Créer une analyse")
    public ResponseEntity<AnalyseSangResponseDTO> create(@Valid @RequestBody AnalyseSangRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(dto));
    }
    @GetMapping
    @Operation(summary = "Lister toutes les analyses")
    public ResponseEntity<List<AnalyseSangResponseDTO>> findAll() {
        return ResponseEntity.ok(service.findAll());
    }
    @GetMapping("/{id}")
    @Operation(summary = "Obtenir une analyse par ID")
    public ResponseEntity<AnalyseSangResponseDTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok(service.findById(id));
    }
    @PutMapping("/{id}")
    @Operation(summary = "Mettre à jour une analyse")
    public ResponseEntity<AnalyseSangResponseDTO> update(@PathVariable Long id, @Valid @RequestBody AnalyseSangRequestDTO dto) {
        return ResponseEntity.ok(service.update(id, dto));
    }
    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer une analyse")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
    @GetMapping("/etat/{etat}")
    @Operation(summary = "Filtrer par état")
    public ResponseEntity<List<AnalyseSangResponseDTO>> findByEtat(@PathVariable EtatAnalyse etat) {
        return ResponseEntity.ok(service.findByEtat(etat));
    }
}
