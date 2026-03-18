package com.project.OPDeckManager.repository;

import com.project.OPDeckManager.domain.entities.Leader;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LeaderRepository extends JpaRepository<Leader, String> {

    List<Leader> findBySetId(String setId);

    List<Leader> findByLeaderLife(String life);

    @Query("SELECT l FROM Leader l WHERE LOWER(l.name) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<Leader> findByNameContainingIgnoreCase(String name);
}
