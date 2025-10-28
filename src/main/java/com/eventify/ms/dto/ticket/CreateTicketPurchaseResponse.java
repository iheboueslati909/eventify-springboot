package com.eventify.ms.dto.ticket;

import java.util.UUID;

public record CreateTicketPurchaseResponse(
    UUID ticketPurchaseId,
    String checkoutUrl,
    String paymentId
) {}