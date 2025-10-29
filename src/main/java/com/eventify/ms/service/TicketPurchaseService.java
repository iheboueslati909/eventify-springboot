package com.eventify.ms.service;

import com.eventify.ms.dto.payment.PaymentSessionResponse;
import com.eventify.ms.dto.ticket.CreateTicketPurchaseRequest;
import com.eventify.ms.dto.ticket.CreateTicketPurchaseResponse;
import com.eventify.ms.dto.ticket.TicketPurchaseResponse;
import com.eventify.ms.enums.TicketPurchaseStatus;
import com.eventify.ms.model.Ticket;
import com.eventify.ms.model.TicketPurchase;
import com.eventify.ms.repository.TicketPurchaseRepository;
import com.eventify.ms.repository.TicketRepository;
import com.eventify.ms.repository.MemberRepository;
import com.eventify.ms.security.SecurityUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
public class TicketPurchaseService {

    private final TicketPurchaseRepository ticketPurchaseRepository;
    private final TicketRepository ticketRepository;
    private final MemberRepository memberRepository;
    private final PaymentService paymentService;
    private final SecurityUtils securityUtils;

    public TicketPurchaseService(
            TicketPurchaseRepository ticketPurchaseRepository,
            TicketRepository ticketRepository,
            MemberRepository memberRepository,
            PaymentService paymentService,
            SecurityUtils securityUtils) {
        this.ticketPurchaseRepository = ticketPurchaseRepository;
        this.ticketRepository = ticketRepository;
        this.memberRepository = memberRepository;
        this.paymentService = paymentService;
        this.securityUtils = securityUtils;
    }

    @Transactional
    public CreateTicketPurchaseResponse createTicketPurchase(CreateTicketPurchaseRequest request) {
        // 1️⃣ Validate ticket
        Ticket ticket = ticketRepository.findById(request.ticketId())
                .orElseThrow(() -> new NoSuchElementException("Ticket not found with id: " + request.ticketId()));

        // 2️⃣ Validate user
        if (!memberRepository.existsById(request.userId())) {
            throw new NoSuchElementException("User not found with id: " + request.userId());
        }

        // 3️⃣ Validate available quantity
        int totalPurchased = ticketPurchaseRepository.getTotalPurchasedQuantityForTicket(ticket.getId());
        if (totalPurchased + request.quantity() > ticket.getQuantity()) {
            throw new IllegalStateException("Not enough tickets available. Requested: " + request.quantity() +
                    ", Available: " + (ticket.getQuantity() - totalPurchased));
        }

        // 4️⃣ Create purchase in PENDING_PAYMENT state
        TicketPurchase ticketPurchase = TicketPurchase.builder()
                .ticket(ticket)
                .userId(request.userId())
                .quantity(request.quantity())
                .totalPrice(ticket.getPrice().multiply(BigDecimal.valueOf(request.quantity())))
                .status(TicketPurchaseStatus.PENDING_PAYMENT)
                .build();

        ticketPurchase = ticketPurchaseRepository.save(ticketPurchase);

        // 5️⃣ Get JWT of current user (to authenticate with your payment gateway)
        String jwtToken = securityUtils.getCurrentUserJwt();

        PaymentSessionResponse paymentSession = paymentService.initiatePaymentSession(
                jwtToken,
                ticketPurchase.getId(),
                request.userId(),
                ticketPurchase.getTotalPrice(),
                "usd",
                request.paymentMethod()
        );

        if (paymentSession == null || paymentSession.paymentId() == null) {
            ticketPurchase.setStatus(TicketPurchaseStatus.CANCELLED);
            ticketPurchaseRepository.save(ticketPurchase);
            throw new IllegalStateException("Payment initiation failed for ticket purchase: " + ticketPurchase.getId());
        }

        ticketPurchase.setPaymentId(paymentSession.paymentId());
        ticketPurchase.setCheckoutUrl(paymentSession.checkoutUrl());
        ticketPurchaseRepository.save(ticketPurchase);

        // 9️⃣ Return response
        return new CreateTicketPurchaseResponse(
                ticketPurchase.getId(),
                paymentSession.checkoutUrl(),
                paymentSession.paymentId()
        );
    }

    @Transactional(readOnly = true)
    public Page<TicketPurchaseResponse> getAllTicketPurchases(Pageable pageable) {
        return ticketPurchaseRepository.findAllWithTicket(pageable)
                .map(this::mapToResponse);
    }

    @Transactional(readOnly = true)
    public TicketPurchaseResponse getTicketPurchaseById(UUID id) {
        TicketPurchase ticketPurchase = ticketPurchaseRepository.findByIdWithTicket(id)
                .orElseThrow(() -> new NoSuchElementException("Ticket purchase not found with id: " + id));
        return mapToResponse(ticketPurchase);
    }

    private TicketPurchaseResponse mapToResponse(TicketPurchase ticketPurchase) {
        return new TicketPurchaseResponse(
                ticketPurchase.getId(),
                ticketPurchase.getTicket().getId(),
                ticketPurchase.getUserId(),
                ticketPurchase.getQuantity(),
                ticketPurchase.getTotalPrice(),
                ticketPurchase.getStatus(),
                ticketPurchase.getCreatedAt()
        );
    }
}
