package com.eventify.ms.service;

import com.eventify.ms.dto.messaging.PaymentProcessedEvent;
import com.eventify.ms.model.TicketPurchase;
import com.eventify.ms.repository.TicketPurchaseRepository;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

import static com.eventify.ms.configuration.RabbitMQConfig.PAYMENT_PROCESSED_QUEUE;

@Service
public class PaymentProcessedEventConsumer {

    private final TicketPurchaseRepository ticketRepo;

    public PaymentProcessedEventConsumer(TicketPurchaseRepository ticketRepo) {
        this.ticketRepo = ticketRepo;
    }

    @Transactional
    @RabbitListener(queues = PAYMENT_PROCESSED_QUEUE)
    public void consume(PaymentProcessedEvent message) {
        System.out.printf("***** PaymentProcessedEventConsumer: %s - %s - %s%n",
                message.paymentId(), message.intentId(), message.status());

        if (!"Succeeded".equalsIgnoreCase(message.status())) {
            return;
        }

        try {
            UUID intentId = UUID.fromString(message.intentId());
            UUID paymentId = UUID.fromString(message.paymentId());

            Optional<TicketPurchase> ticketOpt = ticketRepo.findById(intentId);
            if (ticketOpt.isEmpty()) {
                System.err.printf("No ticket found for intentId=%s%n", intentId);
                return;
            }

            TicketPurchase ticket = ticketOpt.get();
            ticket.setPaymentId(String.valueOf(paymentId));
            ticket.setStatus(com.eventify.ms.enums.TicketPurchaseStatus.PAID);
            ticketRepo.save(ticket);

            System.out.printf("Ticket %s marked as paid.%n", intentId);

        } catch (IllegalArgumentException e) {
            System.err.printf("Invalid UUID format in PaymentProcessedEvent: %s%n", e.getMessage());
        }
    }
}
