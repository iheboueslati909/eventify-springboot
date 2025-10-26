package com.eventify.ms.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.eventify.ms.model.Club;

public interface ClubRepository extends JpaRepository<Club, UUID> {
    @Query("SELECT c FROM Club c LEFT JOIN FETCH c.owners WHERE c.id = :id")
    Optional<Club> findByIdWithOwners(@Param("id") UUID id);
    
    @Query("SELECT DISTINCT c FROM Club c LEFT JOIN FETCH c.owners WHERE c.isDeleted = false")
    Page<Club> findAllActiveWithOwners(Pageable pageable);
    
    boolean existsByNameAndIsDeletedFalse(String name);
}
