package com.project.OPDeckManager.domain.entities;

import jakarta.persistence.Embeddable;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
@Data
@NoArgsConstructor
public class CardColorId implements Serializable {

    @Column(name = "card_set_id")
    private String cardSetId;

    @Column(name = "color_id")
    private Long colorId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CardColorId that = (CardColorId) o;
        return Objects.equals(cardSetId, that.cardSetId) && Objects.equals(colorId, that.colorId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(cardSetId, colorId);
    }
}
