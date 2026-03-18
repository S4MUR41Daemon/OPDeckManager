package com.project.OPDeckManager.domain.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * Class that represents the relationship between a deck and a card, including the number of copies
 * of that card in the deck.
 *
 * @author dmaicas
 */
@Entity
@Table(name = "deck_cards")
@Data
@NoArgsConstructor
@EqualsAndHashCode(exclude = {"deck", "card"})
@ToString(exclude = {"deck", "card"})
public class DeckCard {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "deck_id")
    private Deck deck;

    @ManyToOne
    @JoinColumn(name = "card_set_id")
    private Card card;

    @NotNull
    @Min(1)
    @Max(4)
    @Column(name = "copies")
    private Integer copies;
}
