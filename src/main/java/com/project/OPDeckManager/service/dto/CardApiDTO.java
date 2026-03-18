package com.project.OPDeckManager.service.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * DTO para mapear la respuesta de la API de One Piece TCG
 * Endpoint: https://optcgapi.com/api/sets/card/{card_id}/
 */
@Data
public class CardApiDTO {

    @JsonProperty("card_set_id")
    private String cardSetId;

    @JsonProperty("card_name")
    private String cardName;

    @JsonProperty("card_type")
    private String cardType;

    @JsonProperty("card_color")
    private String cardColor;

    @JsonProperty("card_cost")
    private Integer cardCost;

    @JsonProperty("card_power")
    private String cardPower;

    @JsonProperty("life")
    private String life;

    @JsonProperty("counter_amount")
    private Integer counterAmount;

    @JsonProperty("attribute")
    private String attribute;

    @JsonProperty("sub_types")
    private String subTypes;

    @JsonProperty("card_text")
    private String cardText;

    @JsonProperty("rarity")
    private String rarity;

    @JsonProperty("set_id")
    private String setId;

    @JsonProperty("set_name")
    private String setName;

    @JsonProperty("card_image")
    private String cardImage;

    @JsonProperty("card_image_id")
    private String cardImageId;

    @JsonProperty("market_price")
    private Double marketPrice;

    @JsonProperty("inventory_price")
    private Double inventoryPrice;

    @JsonProperty("date_scraped")
    private String dateScraped;
}
