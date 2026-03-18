package com.project.OPDeckManager.service;

import com.project.OPDeckManager.domain.entities.Card;
import com.project.OPDeckManager.domain.entities.Deck;
import com.project.OPDeckManager.domain.entities.DeckCard;
import com.project.OPDeckManager.repository.CardRepository;
import com.project.OPDeckManager.repository.DeckCardRepository;
import com.project.OPDeckManager.repository.DeckRepository;
import com.project.OPDeckManager.service.dto.CardResponseDTO;
import com.project.OPDeckManager.service.dto.DeckCardDTO;
import com.project.OPDeckManager.service.dto.DeckRequestDTO;
import com.project.OPDeckManager.service.dto.DeckResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service for managing decks. Handles business logic for deck operations, such as creating, updating,
 * deleting decks, and managing the cards within them.
 *
 * @author dmaicas
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DeckService {

    private final DeckRepository deckRepository;
    private final CardRepository cardRepository;
    private final DeckCardRepository deckCardRepository;

    /**
     * Returns all decks from the database.
     */
    public List<DeckResponseDTO> getAllDecks() {
        return deckRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Returns a deck by its ID.
     */
    public Optional<DeckResponseDTO> getDeckById(Long id) {
        return deckRepository.findById(id).map(this::convertToDTO);
    }

    /**
     * Creates a new deck.
     */
    @Transactional
    public DeckResponseDTO createDeck(DeckRequestDTO request) {
        Deck deck = new Deck();
        deck.setName(request.getName());
        deck.setDescription(request.getDescription());
        deck.setCreatedAt(LocalDateTime.now());
        deck.setUpdatedAt(LocalDateTime.now());
        deck.setTotalCards(0);

        Deck savedDeck = deckRepository.save(deck);

        // Add cards to the deck if provided
        if (request.getCards() != null && !request.getCards().isEmpty()) {
            addCardsToDeck(savedDeck.getId(), request.getCards());
        }

        return convertToDTO(savedDeck);
    }

    /**
     * Updates an existing deck.
     */
    @Transactional
    public Optional<DeckResponseDTO> updateDeck(Long id, DeckRequestDTO request) {
        return deckRepository.findById(id).map(deck -> {
            deck.setName(request.getName());
            deck.setDescription(request.getDescription());
            deck.setUpdatedAt(LocalDateTime.now());

            Deck updatedDeck = deckRepository.save(deck);
            return convertToDTO(updatedDeck);
        });
    }

    /**
     * Deletes a deck by its ID.
     */
    @Transactional
    public boolean deleteDeck(Long id) {
        if (!deckRepository.existsById(id)) {
            return false;
        }
        deckRepository.deleteById(id);
        return true;
    }

    /**
     * Adds cards to a deck.
     */
    @Transactional
    public DeckResponseDTO addCardsToDeck(Long deckId, List<com.project.OPDeckManager.service.dto.CardInDeckRequestDTO> cardsRequest) {
        Deck deck = deckRepository.findById(deckId)
                .orElseThrow(() -> new RuntimeException("Deck not found with id: " + deckId));

        int totalCards = 0;

        for (com.project.OPDeckManager.service.dto.CardInDeckRequestDTO cardRequest : cardsRequest) {
            Card card = cardRepository.findById(cardRequest.getCardSetId())
                    .orElseThrow(() -> new RuntimeException("Card not found: " + cardRequest.getCardSetId()));

            DeckCard deckCard = new DeckCard();
            deckCard.setDeck(deck);
            deckCard.setCard(card);
            deckCard.setCopies(cardRequest.getQuantity());

            deckCardRepository.save(deckCard);
            totalCards += cardRequest.getQuantity();
        }

        deck.setTotalCards(totalCards);
        deck.setUpdatedAt(LocalDateTime.now());
        deckRepository.save(deck);

        return convertToDTO(deck);
    }

    /**
     * Removes a card from a deck.
     */
    @Transactional
    public boolean removeCardFromDeck(Long deckId, String cardSetId) {
        List<DeckCard> deckCards = deckCardRepository.findByDeckId(deckId);

        for (DeckCard deckCard : deckCards) {
            if (deckCard.getCard().getCardSetId().equals(cardSetId)) {
                deckCardRepository.delete(deckCard);
                recalculateTotalCards(deckId);
                return true;
            }
        }
        return false;
    }

    /**
     * Recalculates total cards in a deck.
     */
    private void recalculateTotalCards(Long deckId) {
        List<DeckCard> deckCards = deckCardRepository.findByDeckId(deckId);
        int total = deckCards.stream()
                .mapToInt(DeckCard::getQuantity)
                .sum();

        deckRepository.findById(deckId).ifPresent(deck -> {
            deck.setTotalCards(total);
            deck.setUpdatedAt(LocalDateTime.now());
            deckRepository.save(deck);
        });
    }

    /**
     * Converts a Deck entity to DeckResponseDTO.
     */
    private DeckResponseDTO convertToDTO(Deck deck) {
        DeckResponseDTO dto = new DeckResponseDTO();
        dto.setId(deck.getId());
        dto.setName(deck.getName());
        dto.setDescription(deck.getDescription());
        dto.setCreatedAt(deck.getCreatedAt());
        dto.setUpdatedAt(deck.getUpdatedAt());
        dto.setTotalCards(deck.getTotalCards());

        List<DeckCardDTO> cardsDTO = new ArrayList<>();
        for (DeckCard deckCard : deck.getDeckCards()) {
            DeckCardDTO cardDTO = new DeckCardDTO();
            cardDTO.setQuantity(deckCard.getCopies());
            cardDTO.setCard(convertCardToDTO(deckCard.getCard()));
            cardsDTO.add(cardDTO);
        }
        dto.setCards(cardsDTO);

        return dto;
    }

    /**
     * Converts a Card entity to CardResponseDTO.
     */
    private CardResponseDTO convertCardToDTO(Card card) {
        CardResponseDTO dto = new CardResponseDTO();
        dto.setCardSetId(card.getCardSetId());
        dto.setName(card.getName());
        dto.setType(card.getType());
        dto.setCost(card.getCost() != null ? String.valueOf(card.getCost()) : null);
        dto.setPower(card.getPower());
        dto.setLife(card.getLife());
        dto.setCounterAmount(card.getCounterAmount());
        dto.setAttribute(card.getAttribute());
        dto.setSubTypes(card.getSubTypes());
        dto.setCardText(card.getCardText());
        dto.setRarity(card.getRarity());
        dto.setSetId(card.getSetId());
        dto.setSetName(card.getSetName());
        dto.setCardNumber(card.getCardNumber());
        dto.setImageUrl(card.getImageUrl());
        dto.setMarketPrice(card.getMarketPrice());
        return dto;
    }
}
