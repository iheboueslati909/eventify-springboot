package com.eventify.ms.dto.payment;

public record PaymentSessionResponse(
    String checkoutUrl,
    String paymentId
) {}