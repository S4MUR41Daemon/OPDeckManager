package com.project.OPDeckManager.service.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * DTO for representing a card inside a deck with its quantity.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeckCardDTO {
    private Integer quantity;
    private CardResponseDTO card;
}
