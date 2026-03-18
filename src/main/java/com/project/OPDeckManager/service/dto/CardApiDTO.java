package com.project.OPDeckManager.service.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

/**
 * DTO para mapear las cartas del JSON de GitHub:
 * https://raw.githubusercontent.com/nemesis312/OnePieceTCGEngCardList/main/CardDb3.json
 *
 * Campos del JSON: CardNum, Rarity, CardType, Name, Img, Cost, Attribute,
 * Power, Counter, Color, Block, Type, Effect, CardSets, Images
 */
@Data
public class CardApiDTO {

    @JsonProperty("CardNum")
    private String cardNum;

    @JsonProperty("Rarity")
    private String rarity;

    @JsonProperty("CardType")
    private String cardType;

    @JsonProperty("Name")
    private String name;

    @JsonProperty("Img")
    private String img;

    @JsonProperty("Cost")
    private String cost;

    @JsonProperty("Attribute")
    private String attribute;

    @JsonProperty("Power")
    private String power;

    @JsonProperty("Counter")
    private String counter;

    @JsonProperty("Color")
    private String color;

    @JsonProperty("Block")
    private String block;

    @JsonProperty("Type")
    private String type;

    @JsonProperty("Effect")
    private String effect;

    @JsonProperty("CardSets")
    private String cardSets;

    @JsonProperty("Images")
    private List<String> images;
}
