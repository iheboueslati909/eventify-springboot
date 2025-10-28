package com.eventify.ms.service;

import com.eventify.ms.dto.ticket.CreateTicketRequest;
import com.eventify.ms.dto.ticket.TicketResponse;
import com.eventify.ms.model.Ticket;
import com.eventify.ms.repository.TicketRepository;
import com.eventify.ms.repository.EventRepository;
import com.eventify.ms.repository.MemberRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class TicketService {

    private final TicketRepository ticketRepository;
    private final EventRepository eventRepository;
    private final MemberRepository memberRepository;

    public TicketService(TicketRepository ticketRepository, EventRepository eventRepository, MemberRepository memberRepository) {
        this.ticketRepository = ticketRepository;
        this.eventRepository = eventRepository;
        this.memberRepository = memberRepository;
    }

    @Transactional
    public UUID createTicket(CreateTicketRequest request) {
        // Validate event exists
        if (!eventRepository.existsById(request.eventId())) {
            throw new NoSuchElementException("Event not found with id: " + request.eventId());
        }

        // Validate creator exists
        if (!memberRepository.existsById(request.creatorId())) {
            throw new NoSuchElementException("Creator not found with id: " + request.creatorId());
        }

        Ticket ticket = Ticket.builder()
                .eventId(request.eventId())
                .creatorId(request.creatorId())
                .price(request.price())
                .name(request.name())
                .quantity(request.quantity())
                .currency(request.currency())
                .build();

        ticket = ticketRepository.save(ticket);
        return ticket.getId();
    }

    @Transactional(readOnly = true)
    public Page<TicketResponse> getAllTickets(Pageable pageable) {
        Page<Ticket> tickets = ticketRepository.findAll(pageable);
        return tickets.map(this::mapToResponse);
    }

    @Transactional(readOnly = true)
    public TicketResponse getTicketById(UUID id) {
        Ticket ticket = ticketRepository.findByIdWithRelationships(id)
                .orElseThrow(() -> new NoSuchElementException("Ticket not found with id: " + id));
        return mapToResponse(ticket);
    }

    @Transactional
    public TicketResponse updateTicket(UUID id, CreateTicketRequest request) {
        Ticket ticket = ticketRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Ticket not found with id: " + id));

        // If event/creator changed, validate existence
        if (!ticket.getEventId().equals(request.eventId()) && !eventRepository.existsById(request.eventId())) {
            throw new NoSuchElementException("Event not found with id: " + request.eventId());
        }

        if (!ticket.getCreatorId().equals(request.creatorId()) && !memberRepository.existsById(request.creatorId())) {
            throw new NoSuchElementException("Creator not found with id: " + request.creatorId());
        }

        ticket.setEventId(request.eventId());
        ticket.setCreatorId(request.creatorId());
        ticket.setPrice(request.price());
        ticket.setName(request.name());
        ticket.setQuantity(request.quantity());
        ticket.setCurrency(request.currency());

        ticket = ticketRepository.save(ticket);
        return mapToResponse(ticket);
    }

    @Transactional
    public void deleteTicket(UUID id) {
        Ticket ticket = ticketRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Ticket not found with id: " + id));
        // Hard delete (no isDeleted field on Ticket model)
        ticketRepository.delete(ticket);
    }

    private TicketResponse mapToResponse(Ticket ticket) {
        return new TicketResponse(
                ticket.getId(),
                ticket.getEventId(),
                ticket.getCreatorId(),
                ticket.getPrice(),
                ticket.getName(),
                ticket.getQuantity(),
                ticket.getReservedCount(),
                ticket.getCurrency(),
                ticket.getCreatedAt(),
                ticket.getTicketPurchases().stream().map(p -> p.getId()).collect(Collectors.toList())
        );
    }
}
