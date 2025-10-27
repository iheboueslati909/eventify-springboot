package com.eventify.ms.dto.event;

import java.time.OffsetDateTime;
import java.util.UUID;

public record ArtistTimeConflict(
    UUID artistId,
    String artistName,
    OffsetDateTime startTime,
    OffsetDateTime endTime,
    String stageName,
    String eventTitle
) {}