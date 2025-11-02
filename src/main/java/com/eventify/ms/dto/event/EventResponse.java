package com.eventify.ms.dto.event;

import com.eventify.ms.enums.EventStatus;
import com.eventify.ms.enums.EventType;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public record EventResponse(
    UUID id,
    UUID creatorId,
    String title,
    String description,
    OffsetDateTime startDate,
    OffsetDateTime endDate,
    String location,
    EventType type,
    EventStatus status,
    UUID conceptId,
    boolean isDeleted,
    OffsetDateTime createdAt,
    UUID clubId,
    List<UUID> timeTableIds,
    List<UUID> ticketIds
) {}
