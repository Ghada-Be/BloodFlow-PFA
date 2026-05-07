package com.bloodflow.medical.controller;
import com.bloodflow.medical.dto.request.DossierMedicalRequestDTO;
import com.bloodflow.medical.dto.response.DossierMedicalResponseDTO;
import com.bloodflow.medical.service.DossierMedicalService;
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
@RequestMapping("/api/dossiers-medicaux")
@RequiredArgsConstructor
@Tag(name = "Dossiers Médicaux", description = "Gestion des dossiers médicaux des patients")
@SecurityRequirement(name = "bearerAuth")
public class DossierMedicalController {
    private final DossierMedicalService service;
    @PostMapping
    @Operation(summary = "Créer un dossier médical")
    public ResponseEntity<DossierMedicalResponseDTO> create(@Valid @RequestBody DossierMedicalRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(dto));
    }
    @GetMapping
    @Operation(summary = "Lister tous les dossiers")
    public ResponseEntity<List<DossierMedicalResponseDTO>> findAll() {
        return ResponseEntity.ok(service.findAll());
    }
    @GetMapping("/{id}")
    @Operation(summary = "Obtenir un dossier par ID")
    public ResponseEntity<DossierMedicalResponseDTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok(service.findById(id));
    }
    @PutMapping("/{id}")
    @Operation(summary = "Mettre à jour un dossier")
    public ResponseEntity<DossierMedicalResponseDTO> update(@PathVariable Long id, @Valid @RequestBody DossierMedicalRequestDTO dto) {
        return ResponseEntity.ok(service.update(id, dto));
    }
    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer un dossier")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
    @GetMapping("/patient/{patientId}")
    @Operation(summary = "Dossiers d'un patient")
    public ResponseEntity<List<DossierMedicalResponseDTO>> findByPatient(@PathVariable Long patientId) {
        return ResponseEntity.ok(service.findByPatient(patientId));
    }
}
