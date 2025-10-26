package com.eventify.ms.model;

import jakarta.persistence.*;
import lombok.*;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;

import com.eventify.ms.enums.PerformanceType;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "recorded_performances")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class RecordedPerformance {
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(columnDefinition = "uuid", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "media_url")
    private String mediaUrl; // Url flattened

    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    private PerformanceType type;

    @Column(nullable = false, updatable = false)
    @CreationTimestamp
    private OffsetDateTime createdAt;

    @Column(name = "last_modified")
    private OffsetDateTime lastModified;
}
