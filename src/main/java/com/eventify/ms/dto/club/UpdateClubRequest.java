package com.eventify.ms.dto.club;

import jakarta.validation.constraints.*;
import java.util.Set;
import java.util.UUID;

public record UpdateClubRequest(
    @NotBlank(message = "Name is required")
    String name,

    @NotBlank(message = "Address is required")
    String address,

    @Positive(message = "Capacity must be positive")
    Integer capacity,

    Set<UUID> ownerMemberIds
) {}
