package com.eventify.ms.repository;

import com.eventify.ms.model.Concept;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ConceptRepository extends JpaRepository<Concept, UUID> {
    @Query("SELECT c FROM Concept c LEFT JOIN FETCH c.genres WHERE c.id = :id")
    Optional<Concept> findByIdWithGenres(@Param("id") UUID id);

    @Query("SELECT DISTINCT c FROM Concept c LEFT JOIN FETCH c.genres WHERE c.isDeleted = false")
    Page<Concept> findAllActiveWithGenres(Pageable pageable);
}
