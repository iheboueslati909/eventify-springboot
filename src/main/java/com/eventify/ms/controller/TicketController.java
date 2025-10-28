package com.eventify.ms.controller;

import com.eventify.ms.dto.ticket.CreateTicketRequest;
import com.eventify.ms.dto.ticket.TicketResponse;
import com.eventify.ms.service.TicketService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/tickets")
public class TicketController {

    private final TicketService ticketService;

    public TicketController(TicketService ticketService) {
        this.ticketService = ticketService;
    }

    @PostMapping
    public ResponseEntity<Map<String, java.util.UUID>> createTicket(@Valid @RequestBody CreateTicketRequest request) {
        UUID id = ticketService.createTicket(request);
        return ResponseEntity.status(201).body(Map.of("id", id));
    }

    @GetMapping
    public ResponseEntity<Page<TicketResponse>> getAllTickets(@RequestParam(defaultValue = "0") int page,
                                           @RequestParam(defaultValue = "20") int size) {
        Page<TicketResponse> tickets = ticketService.getAllTickets(PageRequest.of(page, size));
        return ResponseEntity.ok(tickets);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TicketResponse> getTicketById(@PathVariable UUID id) {
        TicketResponse resp = ticketService.getTicketById(id);
        return ResponseEntity.ok(resp);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TicketResponse> updateTicket(@PathVariable UUID id, @Valid @RequestBody CreateTicketRequest request) {
        TicketResponse resp = ticketService.updateTicket(id, request);
        return ResponseEntity.ok(resp);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTicket(@PathVariable UUID id) {
        ticketService.deleteTicket(id);
        return ResponseEntity.noContent().build();
    }
}
