package com.project.OPDeckManager.repository;

import com.project.OPDeckManager.domain.entities.UserInventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserInventoryRepository extends JpaRepository<UserInventory, Long> {

    Optional<UserInventory> findByCardCardSetId(String cardSetId);

    List<UserInventory> findByCardCardSetIdIn(List<String> cardSetIds);
}
