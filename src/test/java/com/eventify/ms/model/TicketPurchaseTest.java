package com.eventify.ms.model;

import com.eventify.ms.enums.TicketPurchaseStatus;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;
import java.time.OffsetDateTime;
import java.util.UUID;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class TicketPurchaseTest {
    @InjectMocks
    private TicketPurchase ticketPurchase = TicketPurchase.builder()
        .id(UUID.randomUUID())
        .ticketId(UUID.randomUUID())
        .userId(UUID.randomUUID())
        .paymentId(UUID.randomUUID())
        .status(TicketPurchaseStatus.PENDING_PAYMENT)
        .purchasedAt(OffsetDateTime.now())
        .quantity(2)
        .createdAt(OffsetDateTime.now())
        .ticket(null)
        .user(null)
        .build();

    @Test
    void shouldCreateTicketPurchaseWithValidFields() {
    assertThat(ticketPurchase.getId()).isNotNull();
    assertThat(ticketPurchase.getTicketId()).isNotNull();
    assertThat(ticketPurchase.getUserId()).isNotNull();
    assertThat(ticketPurchase.getPaymentId()).isNotNull();
    assertThat(ticketPurchase.getStatus()).isEqualTo(TicketPurchaseStatus.PENDING_PAYMENT);
    assertThat(ticketPurchase.getPurchasedAt()).isNotNull();
    assertThat(ticketPurchase.getQuantity()).isEqualTo(2);
    assertThat(ticketPurchase.getCreatedAt()).isNotNull();
    assertThat(ticketPurchase.getTicket()).isNull();
    assertThat(ticketPurchase.getUser()).isNull();
    }

    @Test
    void shouldChangeStatus() {
    ticketPurchase.setStatus(TicketPurchaseStatus.PAID);
    assertThat(ticketPurchase.getStatus()).isEqualTo(TicketPurchaseStatus.PAID);
    }

    @Test
    void shouldHandleNullPaymentId() {
        ticketPurchase.setPaymentId(null);
        assertThat(ticketPurchase.getPaymentId()).isNull();
    }

    @Test
    void shouldHandleZeroQuantity() {
        ticketPurchase.setQuantity(0);
        assertThat(ticketPurchase.getQuantity()).isZero();
    }
}
