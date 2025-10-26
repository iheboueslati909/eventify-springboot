package com.eventify.ms.dto.concept;

import java.time.OffsetDateTime;
import java.util.Set;
import java.util.UUID;

import com.eventify.ms.enums.MusicGenre;

public record ConceptResponse(
    UUID id,
    UUID memberId,
    String title,
    String description,
    boolean isDeleted,
    OffsetDateTime createdAt,
    Set<MusicGenre> genres
) {}
