package com.eventify.ms.dto.payment;

import java.math.BigDecimal;
import java.util.UUID;

public record PaymentGatewayRequest(
    BigDecimal amount,
    String currency,
    String paymentMethod,
    UUID intendId,
    UUID userId,
    String idempotencyKey
) {}