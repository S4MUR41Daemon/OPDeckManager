package com.project.OPDeckManager.service;

import com.project.OPDeckManager.domain.entities.Card;
import com.project.OPDeckManager.domain.entities.CardColor;
import com.project.OPDeckManager.domain.entities.Color;
import com.project.OPDeckManager.repository.CardRepository;
import com.project.OPDeckManager.repository.ColorRepository;
import com.project.OPDeckManager.service.dto.CardResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service for managing cards. Handles business logic for card operations.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CardService {

    private final CardRepository cardRepository;
    private final ColorRepository colorRepository;

    /**
     * Returns all cards from the database.
     */
    public List<CardResponseDTO> getAllCards() {
        return cardRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Returns a card by its cardSetId.
     */
    public Optional<CardResponseDTO> getCardById(String cardSetId) {
        return cardRepository.findById(cardSetId).map(this::convertToDTO);
    }

    /**
     * Returns cards filtered by set ID.
     */
    public List<CardResponseDTO> getCardsBySet(String setId) {
        return cardRepository.findBySetId(setId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Returns cards filtered by type.
     */
    public List<CardResponseDTO> getCardsByType(String type) {
        return cardRepository.findByType(type).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Search cards by name (partial match, case insensitive).
     */
    public List<CardResponseDTO> searchCardsByName(String name) {
        return cardRepository.findByNameContainingIgnoreCase(name).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Returns cards filtered by color.
     */
    public List<CardResponseDTO> getCardsByColor(String colorName) {
        Optional<Color> colorOpt = colorRepository.findByName(colorName);
        if (colorOpt.isEmpty()) {
            return new ArrayList<>();
        }

        Color color = colorOpt.get();
        return cardRepository.findAll().stream()
                .filter(card -> card.getCardColors().stream()
                        .anyMatch(cc -> cc.getColor().getId().equals(color.getId())))
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Deletes a card by its cardSetId.
     */
    @Transactional
    public boolean deleteCard(String cardSetId) {
        if (!cardRepository.existsById(cardSetId)) {
            return false;
        }
        cardRepository.deleteById(cardSetId);
        return true;
    }

    /**
     * Converts a Card entity to CardResponseDTO.
     */
    private CardResponseDTO convertToDTO(Card card) {
        CardResponseDTO dto = new CardResponseDTO();
        dto.setCardSetId(card.getCardSetId());
        dto.setName(card.getName());
        dto.setType(card.getType());
        dto.setCost(card.getCost() != null ? String.valueOf(card.getCost()) : null);
        dto.setPower(card.getPower());
        dto.setLife(card.getLife());
        dto.setCounterAmount(card.getCounterAmount());
        dto.setAttribute(card.getAttribute());
        dto.setSubTypes(card.getSubTypes());
        dto.setCardText(card.getCardText());
        dto.setRarity(card.getRarity());
        dto.setSetId(card.getSetId());
        dto.setSetName(card.getSetName());
        dto.setCardNumber(card.getCardNumber());
        dto.setImageUrl(card.getImageUrl());
        dto.setMarketPrice(card.getMarketPrice());

        // Extract colors from CardColor relationship
        List<String> colors = card.getCardColors().stream()
                .map(cc -> cc.getColor().getName())
                .collect(Collectors.toList());
        dto.setColors(colors);

        return dto;
    }
}
