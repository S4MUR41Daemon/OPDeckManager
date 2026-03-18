package com.project.OPDeckManager.service.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for adding a card to a deck. Sent from frontend.
 */
@Data
@NoArgsConstructor
public class CardInDeckRequestDTO {
    private String cardSetId;
    private Integer quantity;
}
