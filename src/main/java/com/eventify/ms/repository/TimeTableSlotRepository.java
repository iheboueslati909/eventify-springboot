package com.eventify.ms.repository;

import com.eventify.ms.model.TimeTableSlot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import com.eventify.ms.dto.event.ArtistTimeConflict;

@Repository
public interface TimeTableSlotRepository extends JpaRepository<TimeTableSlot, UUID> {
    
    @Query("""
        SELECT DISTINCT ap.id FROM TimeTableSlot ts
        JOIN ts.artistProfiles ap
        JOIN ts.timetable t
        JOIN t.event e
        WHERE ap.id IN :artistIds
        AND e.isDeleted = false
        AND (
            (ts.startTime <= :endTime AND ts.endTime >= :startTime)
            OR
            (ts.startTime >= :startTime AND ts.startTime < :endTime)
            OR
            (ts.endTime > :startTime AND ts.endTime <= :endTime)
        )
    """)
    Set<UUID> findArtistsWithConflicts(
        @Param("artistIds") Set<UUID> artistIds,
        @Param("startTime") OffsetDateTime startTime,
        @Param("endTime") OffsetDateTime endTime
    );
    
    @Query("""
        SELECT NEW com.eventify.ms.dto.event.ArtistTimeConflict(
            ap.id,
            ap.artistName,
            ts.startTime,
            ts.endTime,
            t.stageName,
            e.title
        )
        FROM TimeTableSlot ts
        JOIN ts.artistProfiles ap
        JOIN ts.timetable t
        JOIN t.event e
        WHERE ap.id IN :artistIds
        AND e.isDeleted = false
        AND (
            (ts.startTime <= :endTime AND ts.endTime >= :startTime)
            OR
            (ts.startTime >= :startTime AND ts.startTime < :endTime)
            OR
            (ts.endTime > :startTime AND ts.endTime <= :endTime)
        )
    """)
    List<ArtistTimeConflict> findDetailedConflictsForArtists(
        @Param("artistIds") Set<UUID> artistIds,
        @Param("startTime") OffsetDateTime startTime,
        @Param("endTime") OffsetDateTime endTime
    );
}