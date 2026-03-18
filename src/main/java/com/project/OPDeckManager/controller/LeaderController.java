package com.project.OPDeckManager.controller;

import com.project.OPDeckManager.service.LeaderService;
import com.project.OPDeckManager.service.dto.LeaderResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for managing leaders.
 * Provides endpoints for listing, searching, and filtering leader cards.
 */
@RestController
@RequestMapping("/api/leaders")
@RequiredArgsConstructor
public class LeaderController {

    private final LeaderService leaderService;

    /**
     * GET /api/leaders - Returns all leaders.
     * Optional query params: set, name
     */
    @GetMapping
    public ResponseEntity<List<LeaderResponseDTO>> getAllLeaders(
            @RequestParam(required = false) String set,
            @RequestParam(required = false) String name) {

        List<LeaderResponseDTO> leaders;

        if (set != null) {
            leaders = leaderService.getLeadersBySet(set);
        } else if (name != null) {
            leaders = leaderService.searchLeadersByName(name);
        } else {
            leaders = leaderService.getAllLeaders();
        }

        return ResponseEntity.ok(leaders);
    }

    /**
     * GET /api/leaders/{cardSetId} - Returns a leader by its ID.
     */
    @GetMapping("/{cardSetId}")
    public ResponseEntity<LeaderResponseDTO> getLeaderById(@PathVariable String cardSetId) {
        return leaderService.getLeaderById(cardSetId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * DELETE /api/leaders/{cardSetId} - Deletes a leader.
     */
    @DeleteMapping("/{cardSetId}")
    public ResponseEntity<Void> deleteLeader(@PathVariable String cardSetId) {
        boolean deleted = leaderService.deleteLeader(cardSetId);
        if (deleted) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
