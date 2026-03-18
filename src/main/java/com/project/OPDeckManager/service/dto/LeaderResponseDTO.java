package com.project.OPDeckManager.service.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

/**
 * DTO for returning leader data to the frontend.
 * Extends CardResponseDTO with leader-specific fields.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LeaderResponseDTO {
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
    private String leaderLife;  // Campo específico de Leader
}
