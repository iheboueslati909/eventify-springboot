package com.eventify.ms.dto.event;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import com.eventify.ms.enums.EventType;

public record CreateEventRequest(

    @NotBlank(message = "Title is required")
    String title,

    @NotBlank(message = "Description is required")
    String description,

    @NotNull(message = "Start date is required")
    OffsetDateTime  startDate,

    @NotNull(message = "End date is required")
    OffsetDateTime  endDate,

    @NotNull(message = "Location is required")
    String location,

    @NotNull(message = "Type is required")
    EventType type,

    @NotNull(message = "ConceptId is required")
    UUID conceptId,

    @NotNull(message = "TimeTables are required")
    List<TimeTableCreationRequest> timeTables
) {

    public record TimeTableCreationRequest(
        @NotBlank(message = "Stage name is required")
        String stageName,

        @NotNull(message = "Slots are required")
        List<TimeTableSlotRequest> slots
    ) {}

    public record TimeTableSlotRequest(
        @NotNull(message = "Start time is required")
        OffsetDateTime  startTime,

        @NotNull(message = "End time is required")
        OffsetDateTime  endTime,

        @NotBlank(message = "Title is required")
        String title,

        @NotNull(message = "Artist IDs are required")
        List<UUID> artistIds
    ) {}
}