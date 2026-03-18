package com.project.OPDeckManager.service.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO for returning user inventory data to the frontend.
 */
@Data
@NoArgsConstructor
public class UserInventoryResponseDTO {
    private Long id;
    private String cardSetId;
    private String cardName;
    private String cardImageUrl;
    private Integer quantity;
    private BigDecimal purchasePrice;
    private LocalDateTime purchaseDate;
    private String notes;
}
