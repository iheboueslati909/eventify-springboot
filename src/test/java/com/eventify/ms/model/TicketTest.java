package com.eventify.ms.model;

import com.eventify.ms.enums.Currency;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class TicketTest {
    @InjectMocks
    private Ticket ticket = Ticket.builder()
            .id(UUID.randomUUID())
            .eventId(UUID.randomUUID())
            .creatorId(UUID.randomUUID())
            .price(BigDecimal.valueOf(50.00))
            .name("VIP Ticket")
            .quantity(100)
            .reservedCount(0)
            .currency(Currency.USD)
            .createdAt(OffsetDateTime.now())
            .event(null)
            .creator(null)
            .build();

    @Test
    void shouldCreateTicketWithValidFields() {
        assertThat(ticket.getId()).isNotNull();
        assertThat(ticket.getEventId()).isNotNull();
        assertThat(ticket.getCreatorId()).isNotNull();
        assertThat(ticket.getPrice()).isEqualByComparingTo(BigDecimal.valueOf(50.00));
        assertThat(ticket.getName()).isEqualTo("VIP Ticket");
        assertThat(ticket.getQuantity()).isEqualTo(100);
        assertThat(ticket.getReservedCount()).isZero();
        assertThat(ticket.getCurrency()).isEqualTo(Currency.USD);
        assertThat(ticket.getCreatedAt()).isNotNull();
        assertThat(ticket.getEvent()).isNull();
        assertThat(ticket.getCreator()).isNull();
    }

    @Test
    void shouldChangeReservedCount() {
        ticket.setReservedCount(10);
        assertThat(ticket.getReservedCount()).isEqualTo(10);
    }

    @Test
    void shouldHandleNullName() {
        ticket.setName(null);
        assertThat(ticket.getName()).isNull();
    }

    @Test
    void shouldHandleZeroQuantity() {
        ticket.setQuantity(0);
        assertThat(ticket.getQuantity()).isZero();
    }
}
