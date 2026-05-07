package com.bloodflow.medical.controller;
import com.bloodflow.medical.dto.request.ResultatBiologiqueRequestDTO;
import com.bloodflow.medical.dto.response.ResultatBiologiqueResponseDTO;
import com.bloodflow.medical.service.ResultatBiologiqueService;
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
@RequestMapping("/api/resultats-biologiques")
@RequiredArgsConstructor
@Tag(name = "Résultats Biologiques", description = "Gestion des résultats d'analyses")
@SecurityRequirement(name = "bearerAuth")
public class ResultatBiologiqueController {
    private final ResultatBiologiqueService service;
    @PostMapping
    @Operation(summary = "Créer un résultat")
    public ResponseEntity<ResultatBiologiqueResponseDTO> create(@Valid @RequestBody ResultatBiologiqueRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(dto));
    }
    @GetMapping
    @Operation(summary = "Lister tous les résultats")
    public ResponseEntity<List<ResultatBiologiqueResponseDTO>> findAll() { return ResponseEntity.ok(service.findAll()); }
    @GetMapping("/{id}")
    @Operation(summary = "Obtenir un résultat par ID")
    public ResponseEntity<ResultatBiologiqueResponseDTO> findById(@PathVariable Long id) { return ResponseEntity.ok(service.findById(id)); }
    @PutMapping("/{id}")
    @Operation(summary = "Mettre à jour un résultat")
    public ResponseEntity<ResultatBiologiqueResponseDTO> update(@PathVariable Long id, @Valid @RequestBody ResultatBiologiqueRequestDTO dto) {
        return ResponseEntity.ok(service.update(id, dto));
    }
    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer un résultat")
    public ResponseEntity<Void> delete(@PathVariable Long id) { service.delete(id); return ResponseEntity.noContent().build(); }
    @GetMapping("/analyse/{analyseId}")
    @Operation(summary = "Résultat d'une analyse")
    public ResponseEntity<ResultatBiologiqueResponseDTO> findByAnalyse(@PathVariable Long analyseId) {
        return ResponseEntity.ok(service.findByAnalyse(analyseId));
    }
}
