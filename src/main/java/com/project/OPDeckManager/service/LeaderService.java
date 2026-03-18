package com.project.OPDeckManager.service;

import com.project.OPDeckManager.domain.entities.Leader;
import com.project.OPDeckManager.repository.LeaderRepository;
import com.project.OPDeckManager.service.dto.LeaderResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service for managing leaders. Handles business logic for leader operations.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LeaderService {

    private final LeaderRepository leaderRepository;

    /**
     * Returns all leaders from the database.
     */
    public List<LeaderResponseDTO> getAllLeaders() {
        return leaderRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Returns a leader by its cardSetId.
     */
    public Optional<LeaderResponseDTO> getLeaderById(String cardSetId) {
        return leaderRepository.findById(cardSetId).map(this::convertToDTO);
    }

    /**
     * Returns leaders filtered by set ID.
     */
    public List<LeaderResponseDTO> getLeadersBySet(String setId) {
        return leaderRepository.findBySetId(setId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Search leaders by name (partial match, case insensitive).
     */
    public List<LeaderResponseDTO> searchLeadersByName(String name) {
        return leaderRepository.findByNameContainingIgnoreCase(name).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Deletes a leader by its cardSetId.
     */
    @Transactional
    public boolean deleteLeader(String cardSetId) {
        if (!leaderRepository.existsById(cardSetId)) {
            return false;
        }
        leaderRepository.deleteById(cardSetId);
        return true;
    }

    /**
     * Converts a Leader entity to LeaderResponseDTO.
     */
    private LeaderResponseDTO convertToDTO(Leader leader) {
        LeaderResponseDTO dto = new LeaderResponseDTO();
        dto.setCardSetId(leader.getCardSetId());
        dto.setName(leader.getName());
        dto.setType(leader.getType());
        dto.setCost(leader.getCost() != null ? String.valueOf(leader.getCost()) : null);
        dto.setPower(leader.getPower());
        dto.setLife(leader.getLife());
        dto.setCounterAmount(leader.getCounterAmount());
        dto.setAttribute(leader.getAttribute());
        dto.setSubTypes(leader.getSubTypes());
        dto.setCardText(leader.getCardText());
        dto.setRarity(leader.getRarity());
        dto.setSetId(leader.getSetId());
        dto.setSetName(leader.getSetName());
        dto.setCardNumber(leader.getCardNumber());
        dto.setImageUrl(leader.getImageUrl());
        dto.setMarketPrice(leader.getMarketPrice());
        dto.setLeaderLife(leader.getLeaderLife());  // Campo específico de Leader

        // Leaders normalmente no tienen colores, pero lo dejamos por si acaso
        dto.setColors(List.of());

        return dto;
    }
}
