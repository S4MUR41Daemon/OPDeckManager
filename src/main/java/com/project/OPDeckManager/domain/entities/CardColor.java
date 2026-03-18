package com.project.OPDeckManager.domain.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Class that represents an intermediate table between card and color.
 *
 * @author dmaicas
 */

@Entity
@Table(name = "card_colors")
@Data
@NoArgsConstructor
public class CardColor {

    @EmbeddedId
    private CardColorId id;

    @ManyToOne
    @MapsId("cardSetId")
    @JoinColumn(name = "card_set_id")
    private Card card;

    @ManyToOne
    @MapsId("colorId")
    @JoinColumn(name = "color_id")
    private Color color;
}
