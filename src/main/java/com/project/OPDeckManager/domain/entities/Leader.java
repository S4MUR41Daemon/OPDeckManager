package com.project.OPDeckManager.domain.entities;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * Class that represents a Leader card. Leaders have life points and are used as the deck's leader.
 * Extends Card with specific leader attributes.
 * Uses the same 'cards' table as regular cards (SINGLE_TABLE inheritance).
 *
 * @author dmaicas
 */
@Entity
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@DiscriminatorValue("LEADER")
public class Leader extends Card {

    @NotEmpty
    @Column(name = "leader_life")
    private String leaderLife;
}
