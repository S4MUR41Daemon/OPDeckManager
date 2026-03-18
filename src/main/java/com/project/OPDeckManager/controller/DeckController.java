package com.project.OPDeckManager.controller;

import com.project.OPDeckManager.service.DeckService;
import com.project.OPDeckManager.service.dto.CardInDeckRequestDTO;
import com.project.OPDeckManager.service.dto.DeckRequestDTO;
import com.project.OPDeckManager.service.dto.DeckResponseDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for managing decks.
 * Provides endpoints for CRUD operations on decks and managing deck cards.
 */
@RestController
@RequestMapping("/api/decks")
@RequiredArgsConstructor
public class DeckController {

    private final DeckService deckService;

    /**
     * GET /api/decks - Returns all decks.
     */
    @GetMapping
    public ResponseEntity<List<DeckResponseDTO>> getAllDecks() {
        return ResponseEntity.ok(deckService.getAllDecks());
    }

    /**
     * GET /api/decks/{id} - Returns a deck by its ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<DeckResponseDTO> getDeckById(@PathVariable Long id) {
        return deckService.getDeckById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * POST /api/decks - Creates a new deck.
     */
    @PostMapping
    public ResponseEntity<DeckResponseDTO> createDeck(@Valid @RequestBody DeckRequestDTO request) {
        DeckResponseDTO created = deckService.createDeck(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    /**
     * PUT /api/decks/{id} - Updates an existing deck.
     */
    @PutMapping("/{id}")
    public ResponseEntity<DeckResponseDTO> updateDeck(
            @PathVariable Long id,
            @Valid @RequestBody DeckRequestDTO request) {
        return deckService.updateDeck(id, request)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * DELETE /api/decks/{id} - Deletes a deck.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDeck(@PathVariable Long id) {
        boolean deleted = deckService.deleteDeck(id);
        if (deleted) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    /**
     * POST /api/decks/{id}/cards - Adds cards to a deck.
     */
    @PostMapping("/{id}/cards")
    public ResponseEntity<DeckResponseDTO> addCardsToDeck(
            @PathVariable Long id,
            @Valid @RequestBody List<CardInDeckRequestDTO> cardsRequest) {
        try {
            DeckResponseDTO updated = deckService.addCardsToDeck(id, cardsRequest);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * DELETE /api/decks/{id}/cards/{cardSetId} - Removes a card from a deck.
     */
    @DeleteMapping("/{id}/cards/{cardSetId}")
    public ResponseEntity<Void> removeCardFromDeck(
            @PathVariable Long id,
            @PathVariable String cardSetId) {
        boolean removed = deckService.removeCardFromDeck(id, cardSetId);
        if (removed) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
