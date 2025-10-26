package com.eventify.ms.dto.concept;

import jakarta.validation.constraints.NotBlank;
import java.util.Set;

import com.eventify.ms.enums.MusicGenre;

public record UpdateConceptRequest(
    @NotBlank(message = "Title is required")
    String title,

    String description,

    Set<MusicGenre> genres
) {}
