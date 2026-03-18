package com.project.OPDeckManager.domain.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * Class that represents a Leader card. Leaders have life points and are used as the deck's leader.
 * Extends Card with specific leader attributes.
 *
 * @author dmaicas
 */
@Entity
@Table(name = "leaders")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class Leader extends Card {

    @NotEmpty
    @Column(name = "life")
    private String life;
}
