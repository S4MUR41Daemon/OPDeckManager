package com.project.OPDeckManager.repository;

import com.project.OPDeckManager.domain.entities.DeckCard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DeckCardRepository extends JpaRepository<DeckCard, Long> {
    
    List<DeckCard> findByDeckId(Long deckId);
    
    List<DeckCard> findByCardCardSetId(String cardSetId);
}
