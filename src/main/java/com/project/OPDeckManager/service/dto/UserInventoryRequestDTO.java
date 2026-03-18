package com.project.OPDeckManager.service.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO for adding/updating cards in user inventory. Sent from frontend.
 */
@Data
@NoArgsConstructor
public class UserInventoryRequestDTO {
    private String cardSetId;
    private Integer quantity;
    private BigDecimal purchasePrice;
    private LocalDateTime purchaseDate;
    private String notes;
}
