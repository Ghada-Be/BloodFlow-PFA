package com.bloodflow.medical.controller;
import com.bloodflow.medical.dto.request.PrescriptionRequestDTO;
import com.bloodflow.medical.dto.response.PrescriptionResponseDTO;
import com.bloodflow.medical.service.PrescriptionService;
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
@RequestMapping("/api/prescriptions")
@RequiredArgsConstructor
@Tag(name = "Prescriptions", description = "Gestion des prescriptions médicales")
@SecurityRequirement(name = "bearerAuth")
public class PrescriptionController {
    private final PrescriptionService service;
    @PostMapping
    @Operation(summary = "Créer une prescription")
    public ResponseEntity<PrescriptionResponseDTO> create(@Valid @RequestBody PrescriptionRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(dto));
    }
    @GetMapping
    @Operation(summary = "Lister toutes les prescriptions")
    public ResponseEntity<List<PrescriptionResponseDTO>> findAll() { return ResponseEntity.ok(service.findAll()); }
    @GetMapping("/{id}")
    @Operation(summary = "Obtenir une prescription par ID")
    public ResponseEntity<PrescriptionResponseDTO> findById(@PathVariable Long id) { return ResponseEntity.ok(service.findById(id)); }
    @PutMapping("/{id}")
    @Operation(summary = "Mettre à jour une prescription")
    public ResponseEntity<PrescriptionResponseDTO> update(@PathVariable Long id, @Valid @RequestBody PrescriptionRequestDTO dto) {
        return ResponseEntity.ok(service.update(id, dto));
    }
    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer une prescription")
    public ResponseEntity<Void> delete(@PathVariable Long id) { service.delete(id); return ResponseEntity.noContent().build(); }
    @GetMapping("/urgentes")
    @Operation(summary = "Prescriptions urgentes")
    public ResponseEntity<List<PrescriptionResponseDTO>> findUrgentes() { return ResponseEntity.ok(service.findUrgentes()); }
}
