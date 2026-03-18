package com.project.OPDeckManager.service.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * DTO para mapear la respuesta de la API de One Piece TCG
 * Endpoint: https://optcg-api.ryanmichaelhirst.us/api/v1/cards
 * 
 * Nueva estructura de la API (2026):
 * - id: identificador único
 * - code: código de la carta (ej. EB01-001)
 * - rarity: rareza (L, SR, R, C, etc.)
 * - type: tipo (LEADER, CHARACTER, EVENT, STAGE)
 * - name: nombre
 * - cost: coste
 * - attribute: atributo (Slash, Ranged, Strike, Special)
 * - power: poder
 * - counter: counter
 * - color: color (ej. "Red/Green")
 * - class: clase/subtipo
 * - effect: texto de la carta
 * - set: nombre del set
 * - image: URL de la imagen
 */
@Data
public class CardApiDTO {

    @JsonProperty("id")
    private String id;

    @JsonProperty("code")
    private String code;

    @JsonProperty("rarity")
    private String rarity;

    @JsonProperty("type")
    private String type;

    @JsonProperty("name")
    private String name;

    @JsonProperty("cost")
    private Integer cost;

    @JsonProperty("attribute")
    private String attribute;

    @JsonProperty("power")
    private Integer power;

    @JsonProperty("counter")
    private Integer counter;

    @JsonProperty("color")
    private String color;

    @JsonProperty("class")
    private String cardClass;

    @JsonProperty("effect")
    private String effect;

    @JsonProperty("set")
    private String setName;

    @JsonProperty("image")
    private String imageUrl;

    @JsonProperty("_tag")
    private String tag;
}
