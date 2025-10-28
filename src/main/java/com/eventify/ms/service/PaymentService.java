package com.eventify.ms.service;

import org.springframework.stereotype.Service;

import com.eventify.ms.client.PaymentGatewayClient;
import com.eventify.ms.dto.payment.PaymentGatewayRequest;
import com.eventify.ms.dto.payment.PaymentSessionResponse;

import java.math.BigDecimal;
import java.util.UUID;

@Service
public class PaymentService {

    private final PaymentGatewayClient paymentGatewayClient;

    public PaymentService(PaymentGatewayClient paymentGatewayClient) {
        this.paymentGatewayClient = paymentGatewayClient;
    }

    public PaymentSessionResponse initiatePaymentSession(
            String jwtToken, UUID ticketPurchaseId, UUID userId, BigDecimal amount, 
            String currency, String paymentMethod) {

        PaymentGatewayRequest request = new PaymentGatewayRequest(
            amount, currency, paymentMethod, ticketPurchaseId, userId, UUID.randomUUID().toString()
        );

        return paymentGatewayClient.createPaymentSession(jwtToken, request);
    }
}
