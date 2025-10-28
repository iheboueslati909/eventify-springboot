package com.eventify.ms.dto.ticket;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record CreateTicketPurchaseRequest(
    @NotNull(message = "Ticket ID is required")
    UUID ticketId,
    
    @NotNull(message = "User ID is required")
    UUID userId,
    
    @NotNull(message = "Quantity must be specified")
    @Min(value = 1, message = "Quantity must be at least 1")
    Integer quantity,
    
    @NotNull(message = "Payment method is required")
    String paymentMethod
) {}