package com.eventify.ms.dto.concept;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.Set;
import java.util.UUID;

import com.eventify.ms.enums.MusicGenre;

public record CreateConceptRequest(
    @NotNull(message = "memberId is required")
    UUID memberId,

    @NotBlank(message = "Title is required")
    String title,

    String description,

    Set<MusicGenre> genres
){ }
