package com.bloodflow.medical.controller;
import com.bloodflow.medical.dto.request.CommandeSangRequestDTO;
import com.bloodflow.medical.dto.response.CommandeSangResponseDTO;
import com.bloodflow.medical.service.CommandeSangService;
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
@RequestMapping("/api/commandes-sang")
@RequiredArgsConstructor
@Tag(name = "Commandes de Sang", description = "Gestion des commandes de produits sanguins")
@SecurityRequirement(name = "bearerAuth")
public class CommandeSangController {
    private final CommandeSangService service;
    @PostMapping
    @Operation(summary = "Créer une commande")
    public ResponseEntity<CommandeSangResponseDTO> create(@Valid @RequestBody CommandeSangRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(dto));
    }
    @GetMapping
    @Operation(summary = "Lister toutes les commandes")
    public ResponseEntity<List<CommandeSangResponseDTO>> findAll() {
        return ResponseEntity.ok(service.findAll());
    }
    @GetMapping("/{id}")
    @Operation(summary = "Obtenir une commande par ID")
    public ResponseEntity<CommandeSangResponseDTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok(service.findById(id));
    }
    @PutMapping("/{id}")
    @Operation(summary = "Mettre à jour une commande")
    public ResponseEntity<CommandeSangResponseDTO> update(@PathVariable Long id, @Valid @RequestBody CommandeSangRequestDTO dto) {
        return ResponseEntity.ok(service.update(id, dto));
    }
    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer une commande")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
    @GetMapping("/statut/{statut}")
    @Operation(summary = "Filtrer par statut")
    public ResponseEntity<List<CommandeSangResponseDTO>> findByStatut(@PathVariable String statut) {
        return ResponseEntity.ok(service.findByStatut(statut));
    }
}
