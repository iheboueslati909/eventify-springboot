package com.eventify.ms.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import java.time.OffsetDateTime;
import java.util.*;

@Entity
@Table(name = "timetable_slots")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class TimeTableSlot {
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(columnDefinition = "uuid", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "timetable_id", nullable = false)
    private UUID timetableId;

    @Column(name = "start_time", nullable = false)
    private OffsetDateTime startTime;

    @Column(name = "end_time", nullable = false)
    private OffsetDateTime endTime;

    @Column(name = "title")
    private String title; // flattened Title

    // artist profiles many-to-many
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "timetable_slot_artists",
        joinColumns = @JoinColumn(name = "slot_id"),
        inverseJoinColumns = @JoinColumn(name = "artist_profile_id")
    )
    private Set<ArtistProfile> artistProfiles = new HashSet<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "timetable_id", insertable = false, updatable = false)
    private TimeTable timetable;
}
