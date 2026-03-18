package com.project.OPDeckManager.domain.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Class that represents a user's inventory of cards. Tracks quantity and purchase price.
 *
 * @author dmaicas
 */
@Entity
@Table(name = "user_inventory")
@Data
@NoArgsConstructor
public class UserInventory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "card_id", nullable = false)
    private Card card;

    @NotNull
    @Column(name = "quantity")
    private Integer quantity = 0;

    @Column(name = "purchase_price")
    private BigDecimal purchasePrice;

    @Column(name = "purchase_date")
    private LocalDateTime purchaseDate;

    @Column(name = "notes")
    private String notes;
}
