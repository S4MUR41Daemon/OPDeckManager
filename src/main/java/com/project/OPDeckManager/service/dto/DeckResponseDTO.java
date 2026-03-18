package com.project.OPDeckManager.service.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO for returning deck data to the frontend.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeckResponseDTO {
    private Long id;
    private String name;
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Integer totalCards;
    private List<DeckCardDTO> cards;
}
