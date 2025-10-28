package com.eventify.ms.dto.ticket;

import com.eventify.ms.enums.TicketPurchaseStatus;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

public record TicketPurchaseResponse(
    UUID id,
    UUID ticketId,
    UUID userId,
    Integer quantity,
    BigDecimal totalPrice,
    TicketPurchaseStatus status,
    OffsetDateTime createdAt
) {}