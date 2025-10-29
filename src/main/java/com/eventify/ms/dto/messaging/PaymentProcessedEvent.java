package com.eventify.ms.dto.messaging;


import java.math.BigDecimal;
import java.time.OffsetDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;

public record PaymentProcessedEvent(
    @JsonProperty("PaymentId")
    String paymentId,
    @JsonProperty("IntentId")
    String intentId,
    @JsonProperty("AppId")
    String appId,
    @JsonProperty("Amount")
    BigDecimal amount,
    @JsonProperty("UserId")
    String userId,
    @JsonProperty("ProcessedAt")
    OffsetDateTime processedAt,
    @JsonProperty("Status")
    String status
) {}
