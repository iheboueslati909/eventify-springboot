package com.eventify.ms.model;

import jakarta.persistence.*;
import lombok.*;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;

import com.eventify.ms.enums.TicketPurchaseStatus;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "ticket_purchases")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class TicketPurchase {
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(columnDefinition = "uuid", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "ticket_id", nullable = false)
    private UUID ticketId;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "payment_id")
    private UUID paymentId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private TicketPurchaseStatus status;

    @Column(name = "purchased_at", nullable = false)
    private OffsetDateTime purchasedAt;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @Column(nullable = false, updatable = false)
    @CreationTimestamp
    private OffsetDateTime createdAt;

    // navigations
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ticket_id", insertable = false, updatable = false)
    private Ticket ticket;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private Member user;
}
