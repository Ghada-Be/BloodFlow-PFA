package com.bloodflow.medical.controller;
import com.bloodflow.medical.dto.request.LivraisonRequestDTO;
import com.bloodflow.medical.dto.response.LivraisonResponseDTO;
import com.bloodflow.medical.service.LivraisonService;
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
@RequestMapping("/api/livraisons")
@RequiredArgsConstructor
@Tag(name = "Livraisons", description = "Gestion des livraisons de produits sanguins")
@SecurityRequirement(name = "bearerAuth")
public class LivraisonController {
    private final LivraisonService service;
    @PostMapping
    @Operation(summary = "Créer une livraison")
    public ResponseEntity<LivraisonResponseDTO> create(@Valid @RequestBody LivraisonRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(dto));
    }
    @GetMapping
    @Operation(summary = "Lister toutes les livraisons")
    public ResponseEntity<List<LivraisonResponseDTO>> findAll() { return ResponseEntity.ok(service.findAll()); }
    @GetMapping("/{id}")
    @Operation(summary = "Obtenir une livraison par ID")
    public ResponseEntity<LivraisonResponseDTO> findById(@PathVariable Long id) { return ResponseEntity.ok(service.findById(id)); }
    @PutMapping("/{id}")
    @Operation(summary = "Mettre à jour une livraison")
    public ResponseEntity<LivraisonResponseDTO> update(@PathVariable Long id, @Valid @RequestBody LivraisonRequestDTO dto) {
        return ResponseEntity.ok(service.update(id, dto));
    }
    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer une livraison")
    public ResponseEntity<Void> delete(@PathVariable Long id) { service.delete(id); return ResponseEntity.noContent().build(); }
    @GetMapping("/statut/{statut}")
    @Operation(summary = "Filtrer par statut")
    public ResponseEntity<List<LivraisonResponseDTO>> findByStatut(@PathVariable String statut) {
        return ResponseEntity.ok(service.findByStatut(statut));
    }
}
