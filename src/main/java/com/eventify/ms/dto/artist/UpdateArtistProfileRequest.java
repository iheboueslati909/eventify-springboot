package com.eventify.ms.dto.artist;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Email;
import java.util.Set;

import com.eventify.ms.enums.MusicGenre;

public record UpdateArtistProfileRequest(
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