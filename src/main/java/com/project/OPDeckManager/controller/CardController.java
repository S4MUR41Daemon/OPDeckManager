package com.project.OPDeckManager.controller;

import com.project.OPDeckManager.service.CardService;
import com.project.OPDeckManager.service.dto.CardResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for managing cards.
 * Provides endpoints for listing, searching, and filtering cards.
 */
@RestController
@RequestMapping("/api/cards")
@RequiredArgsConstructor
public class CardController {

    private final CardService cardService;

    /**
     * GET /api/cards - Returns all cards.
     * Optional query params: set, type, color, name
     */
    @GetMapping
    public ResponseEntity<List<CardResponseDTO>> getAllCards(
            @RequestParam(required = false) String set,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String color,
            @RequestParam(required = false) String name) {

        List<CardResponseDTO> cards;

        if (set != null) {
            cards = cardService.getCardsBySet(set);
        } else if (type != null) {
            cards = cardService.getCardsByType(type);
        } else if (color != null) {
            cards = cardService.getCardsByColor(color);
        } else if (name != null) {
            cards = cardService.searchCardsByName(name);
        } else {
            cards = cardService.getAllCards();
        }

        return ResponseEntity.ok(cards);
    }

    /**
     * GET /api/cards/{cardSetId} - Returns a card by its ID.
     */
    @GetMapping("/{cardSetId}")
    public ResponseEntity<CardResponseDTO> getCardById(@PathVariable String cardSetId) {
        return cardService.getCardById(cardSetId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * DELETE /api/cards/{cardSetId} - Deletes a card.
     */
    @DeleteMapping("/{cardSetId}")
    public ResponseEntity<Void> deleteCard(@PathVariable String cardSetId) {
        boolean deleted = cardService.deleteCard(cardSetId);
        if (deleted) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
