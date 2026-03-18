package com.project.OPDeckManager.controller;

import com.project.OPDeckManager.service.UserInventoryService;
import com.project.OPDeckManager.service.dto.UserInventoryRequestDTO;
import com.project.OPDeckManager.service.dto.UserInventoryResponseDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for managing user inventory.
 * Provides endpoints for CRUD operations on the user's card collection.
 */
@RestController
@RequestMapping("/api/inventory")
@RequiredArgsConstructor
public class UserInventoryController {

    private final UserInventoryService userInventoryService;

    /**
     * GET /api/inventory - Returns all cards in the user's inventory.
     */
    @GetMapping
    public ResponseEntity<List<UserInventoryResponseDTO>> getAllInventory() {
        return ResponseEntity.ok(userInventoryService.getAllInventory());
    }

    /**
     * GET /api/inventory/{id} - Returns a specific inventory entry.
     */
    @GetMapping("/{id}")
    public ResponseEntity<UserInventoryResponseDTO> getInventoryEntry(@PathVariable Long id) {
        return userInventoryService.getInventoryEntry(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * POST /api/inventory - Adds a card to inventory or updates quantity.
     */
    @PostMapping
    public ResponseEntity<UserInventoryResponseDTO> addOrUpdateInventory(
            @Valid @RequestBody UserInventoryRequestDTO request) {
        try {
            UserInventoryResponseDTO created = userInventoryService.addOrUpdateInventory(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * PUT /api/inventory/{id} - Updates an inventory entry.
     */
    @PutMapping("/{id}")
    public ResponseEntity<UserInventoryResponseDTO> updateInventory(
            @PathVariable Long id,
            @Valid @RequestBody UserInventoryRequestDTO request) {
        return userInventoryService.updateInventory(id, request)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * DELETE /api/inventory/{id} - Removes a card from inventory.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> removeFromInventory(@PathVariable Long id) {
        boolean removed = userInventoryService.removeFromInventory(id);
        if (removed) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
