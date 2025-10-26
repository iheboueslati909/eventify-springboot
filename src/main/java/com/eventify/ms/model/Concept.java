package com.eventify.ms.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import java.util.*;

@Entity
@Table(name = "concepts")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Concept {
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(columnDefinition = "uuid", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "member_id", nullable = false)
    private UUID memberId;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "description", columnDefinition = "text")
    private String description;

    @Column(name = "is_deleted", nullable = false)
    private boolean isDeleted = false;

    @ElementCollection(targetClass = com.eventify.ms.enums.MusicGenre.class, fetch = FetchType.LAZY)
    @CollectionTable(name = "concept_genres", joinColumns = @JoinColumn(name = "concept_id"))
    @Column(name = "genre")
    @Enumerated(EnumType.STRING)
    private Set<com.eventify.ms.enums.MusicGenre> genres = new HashSet<>();
}
