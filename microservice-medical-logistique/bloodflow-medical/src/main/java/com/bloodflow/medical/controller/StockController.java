package com.bloodflow.medical.controller;
import com.bloodflow.medical.dto.request.StockRequestDTO;
import com.bloodflow.medical.dto.response.StockResponseDTO;
import com.bloodflow.medical.service.StockService;
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
@RequestMapping("/api/stocks")
@RequiredArgsConstructor
@Tag(name = "Stock", description = "Gestion du stock de produits sanguins")
@SecurityRequirement(name = "bearerAuth")
public class StockController {
    private final StockService service;
    @PostMapping
    @Operation(summary = "Créer un stock")
    public ResponseEntity<StockResponseDTO> create(@Valid @RequestBody StockRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(dto));
    }
    @GetMapping
    @Operation(summary = "Lister tous les stocks")
    public ResponseEntity<List<StockResponseDTO>> findAll() { return ResponseEntity.ok(service.findAll()); }
    @GetMapping("/{id}")
    @Operation(summary = "Obtenir un stock par ID")
    public ResponseEntity<StockResponseDTO> findById(@PathVariable Long id) { return ResponseEntity.ok(service.findById(id)); }
    @PutMapping("/{id}")
    @Operation(summary = "Mettre à jour un stock")
    public ResponseEntity<StockResponseDTO> update(@PathVariable Long id, @Valid @RequestBody StockRequestDTO dto) {
        return ResponseEntity.ok(service.update(id, dto));
    }
    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer un stock")
    public ResponseEntity<Void> delete(@PathVariable Long id) { service.delete(id); return ResponseEntity.noContent().build(); }
    @GetMapping("/alertes")
    @Operation(summary = "Stocks en alerte (sous le seuil)")
    public ResponseEntity<List<StockResponseDTO>> findEnAlerte() { return ResponseEntity.ok(service.findStocksEnAlerte()); }
}
