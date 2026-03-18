package com.project.OPDeckManager.service;

import com.project.OPDeckManager.domain.entities.Card;
import com.project.OPDeckManager.domain.entities.UserInventory;
import com.project.OPDeckManager.repository.CardRepository;
import com.project.OPDeckManager.repository.UserInventoryRepository;
import com.project.OPDeckManager.service.dto.UserInventoryRequestDTO;
import com.project.OPDeckManager.service.dto.UserInventoryResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service for managing user inventory. Handles business logic for inventory operations.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserInventoryService {

    private final UserInventoryRepository userInventoryRepository;
    private final CardRepository cardRepository;

    /**
     * Returns all cards in the user's inventory.
     */
    public List<UserInventoryResponseDTO> getAllInventory() {
        return userInventoryRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Returns a specific inventory entry by ID.
     */
    public Optional<UserInventoryResponseDTO> getInventoryEntry(Long id) {
        return userInventoryRepository.findById(id).map(this::convertToDTO);
    }

    /**
     * Adds a card to the user's inventory or updates quantity if it already exists.
     */
    @Transactional
    public UserInventoryResponseDTO addOrUpdateInventory(UserInventoryRequestDTO request) {
        // Verify the card exists
        Card card = cardRepository.findById(request.getCardSetId())
                .orElseThrow(() -> new RuntimeException("Card not found: " + request.getCardSetId()));

        // Check if the card is already in inventory
        Optional<UserInventory> existingOpt = userInventoryRepository.findByCardCardSetId(request.getCardSetId());

        UserInventory inventory;
        if (existingOpt.isPresent()) {
            // Update existing entry
            inventory = existingOpt.get();
            int newQuantity = inventory.getQuantity() + request.getQuantity();
            inventory.setQuantity(newQuantity);
            
            // Update price if provided
            if (request.getPurchasePrice() != null) {
                inventory.setPurchasePrice(request.getPurchasePrice());
            }
            if (request.getPurchaseDate() != null) {
                inventory.setPurchaseDate(request.getPurchaseDate());
            }
            if (request.getNotes() != null) {
                inventory.setNotes(request.getNotes());
            }
        } else {
            // Create new entry
            inventory = new UserInventory();
            inventory.setCard(card);
            inventory.setQuantity(request.getQuantity());
            inventory.setPurchasePrice(request.getPurchasePrice());
            inventory.setPurchaseDate(request.getPurchaseDate());
            inventory.setNotes(request.getNotes());
        }

        userInventoryRepository.save(inventory);
        return convertToDTO(inventory);
    }

    /**
     * Updates an inventory entry.
     */
    @Transactional
    public Optional<UserInventoryResponseDTO> updateInventory(Long id, UserInventoryRequestDTO request) {
        return userInventoryRepository.findById(id).map(inventory -> {
            if (request.getQuantity() != null) {
                inventory.setQuantity(request.getQuantity());
            }
            if (request.getPurchasePrice() != null) {
                inventory.setPurchasePrice(request.getPurchasePrice());
            }
            if (request.getPurchaseDate() != null) {
                inventory.setPurchaseDate(request.getPurchaseDate());
            }
            if (request.getNotes() != null) {
                inventory.setNotes(request.getNotes());
            }

            userInventoryRepository.save(inventory);
            return convertToDTO(inventory);
        });
    }

    /**
     * Removes a card from the user's inventory.
     */
    @Transactional
    public boolean removeFromInventory(Long id) {
        if (!userInventoryRepository.existsById(id)) {
            return false;
        }
        userInventoryRepository.deleteById(id);
        return true;
    }

    /**
     * Converts a UserInventory entity to UserInventoryResponseDTO.
     */
    private UserInventoryResponseDTO convertToDTO(UserInventory inventory) {
        UserInventoryResponseDTO dto = new UserInventoryResponseDTO();
        dto.setId(inventory.getId());
        dto.setCardSetId(inventory.getCard().getCardSetId());
        dto.setCardName(inventory.getCard().getName());
        dto.setCardImageUrl(inventory.getCard().getImageUrl());
        dto.setQuantity(inventory.getQuantity());
        dto.setPurchasePrice(inventory.getPurchasePrice());
        dto.setPurchaseDate(inventory.getPurchaseDate());
        dto.setNotes(inventory.getNotes());
        return dto;
    }
}
