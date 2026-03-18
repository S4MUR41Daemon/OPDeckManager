package com.project.OPDeckManager.repository;

import com.project.OPDeckManager.domain.entities.Card;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CardRepository extends JpaRepository<Card, String> {

    @Query("SELECT c FROM Card c WHERE c.cardSetId IN :ids")
    List<Card> findByIdIn(@Param("ids") List<String> ids);

    List<Card> findBySetId(String setId);

    List<Card> findByType(String type);

    Optional<Card> findByName(String name);

    @Query("SELECT c FROM Card c WHERE LOWER(c.name) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<Card> findByNameContainingIgnoreCase(@Param("name") String name);
}
