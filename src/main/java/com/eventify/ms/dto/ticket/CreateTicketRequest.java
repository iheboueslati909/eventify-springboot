package com.eventify.ms.dto.ticket;

import com.eventify.ms.enums.Currency;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.NotBlank;
import java.math.BigDecimal;
import java.util.UUID;

public record CreateTicketRequest(
    @NotNull
    UUID eventId,

    @NotNull
    UUID creatorId,

    @NotNull
    BigDecimal price,

    @NotBlank
    String name,

    @NotNull
    @PositiveOrZero
    Integer quantity,

    @NotNull
    Currency currency
) {}
