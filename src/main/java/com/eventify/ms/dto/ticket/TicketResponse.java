package com.eventify.ms.dto.ticket;

import com.eventify.ms.enums.Currency;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public record TicketResponse(
    UUID id,
    UUID eventId,
    UUID creatorId,
    BigDecimal price,
    String name,
    Integer quantity,
    Integer reservedCount,
    Currency currency,
    OffsetDateTime createdAt,
    List<UUID> ticketPurchaseIds
) {}
