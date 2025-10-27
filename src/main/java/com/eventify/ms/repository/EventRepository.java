package com.eventify.ms.repository;

import com.eventify.ms.model.Event;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;
import java.util.UUID;

@Repository
public interface EventRepository extends JpaRepository<Event, UUID> {
    
    @Query("SELECT e FROM Event e " +
           "LEFT JOIN FETCH e.timetables t " +
           "LEFT JOIN FETCH t.slots s " +
           "LEFT JOIN FETCH s.artistProfiles " +
           "WHERE e.id = :id AND e.isDeleted = false")
    Optional<Event> findByIdWithRelationships(@Param("id") UUID id);

    @Query("SELECT DISTINCT e FROM Event e " +
           "LEFT JOIN FETCH e.timetables " +
           "WHERE e.isDeleted = false")
    Page<Event> findAllActiveWithTimeTables(Pageable pageable);

    @Query("SELECT DISTINCT e FROM Event e " +
           "LEFT JOIN FETCH e.timetables t " +
           "LEFT JOIN FETCH t.slots s " +
           "LEFT JOIN FETCH s.artistProfiles ap " +
           "WHERE ap.id = :artistId AND e.isDeleted = false")
    List<Event> findByArtistId(@Param("artistId") UUID artistId);

       @Query("SELECT DISTINCT e FROM Event e " +
                 "LEFT JOIN FETCH e.timetables t " +
                 "LEFT JOIN FETCH t.slots s " +
                 "LEFT JOIN FETCH s.artistProfiles ap " +
                 "WHERE e.conceptId = :conceptId AND e.isDeleted = false")
       List<Event> findByConceptIdWithRelationships(@Param("conceptId") UUID conceptId);
}