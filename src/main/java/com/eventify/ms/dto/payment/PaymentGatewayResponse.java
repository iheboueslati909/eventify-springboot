package com.eventify.ms.dto.payment;

public record PaymentGatewayResponse(
    String checkoutUrl,
    String paymentId
) {}