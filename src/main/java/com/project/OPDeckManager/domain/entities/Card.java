package com.project.OPDeckManager.domain.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;


/**
 * Class that represents a card of One Piece TCG imported from the API. It is related with DeckCard and CardColor
 *
 * @author dmaicas
 */

@Entity
@Table(name = "cards")
@Data
@NoArgsConstructor
public class Card {

    @Id
    @Column(name = "card_set_id")
    private String cardSetId;

    @NotEmpty
    @Column(name = "name")
    private String name;

    @NotEmpty
    @Column(name = "type")
    private String type;

    @Column(name = "cost")
    private Integer cost;

    @Column(name = "power")
    private Integer power;

    @Column(name = "life")
    private Integer life;

    @Column(name = "counter_amount")
    private Integer counterAmount;

    @Column(name = "attribute")
    private String attribute;

    @Column(name = "sub_types")
    private String subTypes;

    @Column(name = "card_text", columnDefinition = "TEXT")
    private String cardText;

    @Column(name = "rarity")
    private String rarity;

    @NotEmpty
    @Column(name = "set_id")
    private String setId;

    @Column(name = "set_name")
    private String setName;

    @Column(name = "card_number")
    private String cardNumber;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(name = "image_id")
    private String imageId;

    @Column(name = "market_price")
    private BigDecimal marketPrice;

    @Column(name = "inventory_price")
    private BigDecimal inventoryPrice;

    @NotNull
    @Column(name = "imported_at")
    private LocalDateTime importedAt;

    @OneToMany(mappedBy = "card", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<CardColor> cardColors = new HashSet<>();

    @OneToMany(mappedBy = "card", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<DeckCard> deckCards = new HashSet<>();
}
