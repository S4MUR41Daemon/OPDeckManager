package com.project.OPDeckManager.domain.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * Class that represents an intermediate table between card and color.
 *
 * @author dmaicas
 */

@Entity
@Table(name = "card_colors")
@Data
@NoArgsConstructor
@EqualsAndHashCode(exclude = {"card", "color"})
@ToString(exclude = {"card", "color"})
public class CardColor {

    @EmbeddedId
    private CardColorId id;

    @JsonIgnore
    @ManyToOne
    @MapsId("cardSetId")
    @JoinColumn(name = "card_set_id")
    private Card card;

    @ManyToOne
    @MapsId("colorId")
    @JoinColumn(name = "color_id")
    private Color color;
}
