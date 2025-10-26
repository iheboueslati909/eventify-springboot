package com.eventify.ms.dto.artist;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Email;
import java.util.Set;
import java.util.UUID;

import com.eventify.ms.enums.MusicGenre;

public record CreateArtistProfileRequest(
    @NotNull(message = "memberId is required")
    UUID memberId,

    @NotBlank(message = "Artist name is required")
    String artistName,

    String bio,

    Set<MusicGenre> genres,

    @Email(message = "Must be a valid email address")
    String email,

    String socialInstagram,
    String socialFacebook,
    String socialTwitter,
    String socialSoundcloud
) {}