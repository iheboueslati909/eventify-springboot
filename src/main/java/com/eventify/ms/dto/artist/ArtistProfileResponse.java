package com.eventify.ms.dto.artist;

import java.util.Set;
import java.util.UUID;

import com.eventify.ms.enums.MusicGenre;

public record ArtistProfileResponse(
    UUID id,
    UUID memberId,
    String artistName,
    String bio,
    boolean isDeleted,
    Set<MusicGenre> genres,
    String email,
    String socialInstagram,
    String socialFacebook,
    String socialTwitter,
    String socialSoundcloud
) {}