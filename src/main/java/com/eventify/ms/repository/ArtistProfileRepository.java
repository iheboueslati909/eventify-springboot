package com.eventify.ms.repository;

import com.eventify.ms.model.ArtistProfile;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ArtistProfileRepository extends JpaRepository<ArtistProfile, UUID> {
    @Query("SELECT a FROM ArtistProfile a LEFT JOIN FETCH a.genres WHERE a.id = :id")
    Optional<ArtistProfile> findByIdWithGenres(@Param("id") UUID id);

    @Query("SELECT DISTINCT a FROM ArtistProfile a LEFT JOIN FETCH a.genres WHERE a.isDeleted = false")
    Page<ArtistProfile> findAllActiveWithGenres(Pageable pageable);
    
    Optional<ArtistProfile> findByMemberIdAndIsDeletedFalse(UUID memberId);
}