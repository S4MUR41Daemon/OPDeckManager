package com.project.OPDeckManager.domain.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

/**
 * Class that represents one of the six colors of the game. It is related with CardColor.
 *
 * @author dmaicas
 */

@Entity
@Table(name = "colors")
@Data
@NoArgsConstructor
public class Color {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotEmpty
    @Column(name = "name", unique = true)
    private String name;

    @OneToMany(mappedBy = "color", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<CardColor> cardColors = new HashSet<>();

    public Color(String name) {
        this.name = name;
    }
}
