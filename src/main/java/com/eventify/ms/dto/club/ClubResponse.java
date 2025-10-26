package com.eventify.ms.dto.club;

import java.util.Set;
import java.util.UUID;

public record ClubResponse(
    UUID id,
    String name,
    String address,
    Integer capacity,
    boolean isDeleted,
    Set<UUID> ownerIds
) {}
