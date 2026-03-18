package com.project.OPDeckManager.service.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

/**
 * DTO for returning card data to the frontend.
 * Does not expose internal fields like importedAt or inventoryPrice.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CardResponseDTO {
    private String cardSetId;
    private String name;
    private String type;
    private String cost;
    private Integer power;
    private Integer life;
    private Integer counterAmount;
    private String attribute;
    private String subTypes;
    private String cardText;
    private String rarity;
    private String setId;
    private String setName;
    private String cardNumber;
    private String imageUrl;
    private BigDecimal marketPrice;
    private List<String> colors;
}
