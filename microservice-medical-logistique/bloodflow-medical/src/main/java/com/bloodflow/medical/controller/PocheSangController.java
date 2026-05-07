package com.bloodflow.medical.controller;
import com.bloodflow.medical.dto.request.PocheSangRequestDTO;
import com.bloodflow.medical.dto.response.PocheSangResponseDTO;
import com.bloodflow.medical.entity.StatutPoche;
import com.bloodflow.medical.service.PocheSangService;
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
@RequestMapping("/api/poches-sang")
@RequiredArgsConstructor
@Tag(name = "Poches de Sang", description = "Gestion des poches de sang")
@SecurityRequirement(name = "bearerAuth")
public class PocheSangController {
    private final PocheSangService service;
    @PostMapping
    @Operation(summary = "Créer une poche de sang")
    public ResponseEntity<PocheSangResponseDTO> create(@Valid @RequestBody PocheSangRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(dto));
    }
    @GetMapping
    @Operation(summary = "Lister toutes les poches")
    public ResponseEntity<List<PocheSangResponseDTO>> findAll() { return ResponseEntity.ok(service.findAll()); }
    @GetMapping("/{id}")
    @Operation(summary = "Obtenir une poche par ID")
    public ResponseEntity<PocheSangResponseDTO> findById(@PathVariable Long id) { return ResponseEntity.ok(service.findById(id)); }
    @PutMapping("/{id}")
    @Operation(summary = "Mettre à jour une poche")
    public ResponseEntity<PocheSangResponseDTO> update(@PathVariable Long id, @Valid @RequestBody PocheSangRequestDTO dto) {
        return ResponseEntity.ok(service.update(id, dto));
    }
    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer une poche")
    public ResponseEntity<Void> delete(@PathVariable Long id) { service.delete(id); return ResponseEntity.noContent().build(); }
    @GetMapping("/statut/{statut}")
    @Operation(summary = "Filtrer par statut")
    public ResponseEntity<List<PocheSangResponseDTO>> findByStatut(@PathVariable StatutPoche statut) {
        return ResponseEntity.ok(service.findByStatut(statut));
    }
    @GetMapping("/disponibles/{groupeSanguin}")
    @Operation(summary = "Poches disponibles par groupe")
    public ResponseEntity<List<PocheSangResponseDTO>> findDisponibles(@PathVariable String groupeSanguin) {
        return ResponseEntity.ok(service.findDisponiblesByGroupe(groupeSanguin));
    }
}
