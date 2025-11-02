package com.eventify.ms.controller;

import com.eventify.ms.dto.ticket.CreateTicketRequest;
import com.eventify.ms.dto.ticket.TicketResponse;
import com.eventify.ms.dto.ticket.UpdateTicketRequest;
import com.eventify.ms.service.TicketService;
import com.eventify.ms.service.auth.JwtService;
import com.eventify.ms.exception.InvalidTokenException;
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
    private final JwtService jwtService;

    public TicketController(TicketService ticketService, JwtService jwtService) {
        this.ticketService = ticketService;
        this.jwtService = jwtService;
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
    public ResponseEntity<TicketResponse> updateTicket(@PathVariable UUID id, @Valid @RequestBody UpdateTicketRequest request,
                                                       @RequestHeader("Authorization") String authHeader) {
        String token = jwtService.extractTokenFromString(authHeader);
        java.util.UUID userId = jwtService.extractUserId(token).orElseThrow(() -> new InvalidTokenException("Invalid token"));
        TicketResponse resp = ticketService.updateTicket(id, request, userId);
        return ResponseEntity.ok(resp);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTicket(@PathVariable UUID id, @RequestHeader("Authorization") String authHeader) {
        String token = jwtService.extractTokenFromString(authHeader);
        java.util.UUID userId = jwtService.extractUserId(token).orElseThrow(() -> new InvalidTokenException("Invalid token"));
        ticketService.deleteTicket(id, userId);
        return ResponseEntity.noContent().build();
    }
}
