package com.eventify.ms.dto.member;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public record MemberResponse(
    UUID id,
    String firstName,
    String lastName,
    String email,
    boolean isDeleted,
    OffsetDateTime createdAt,
    UUID userId,
    List<UUID> artistProfileIds,
    List<UUID> ticketIds,
    List<UUID> ticketPurchaseIds
) {}