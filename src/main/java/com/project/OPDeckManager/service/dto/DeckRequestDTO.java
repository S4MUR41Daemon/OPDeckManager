package com.project.OPDeckManager.service.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO for creating/updating a deck. Sent from frontend.
 */
@Data
@NoArgsConstructor
public class DeckRequestDTO {
    private String name;
    private String description;
    private List<CardInDeckRequestDTO> cards;
}
