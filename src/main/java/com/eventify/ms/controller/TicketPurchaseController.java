package com.eventify.ms.controller;

import com.eventify.ms.dto.ticket.CreateTicketPurchaseRequest;
import com.eventify.ms.dto.ticket.CreateTicketPurchaseResponse;
import com.eventify.ms.dto.ticket.TicketPurchaseResponse;
import com.eventify.ms.service.TicketPurchaseService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/ticket-purchases")
public class TicketPurchaseController {
    
    private final TicketPurchaseService ticketPurchaseService;

    public TicketPurchaseController(TicketPurchaseService ticketPurchaseService) {
        this.ticketPurchaseService = ticketPurchaseService;
    }

    @PostMapping
    public ResponseEntity<CreateTicketPurchaseResponse> createTicketPurchase(@Valid @RequestBody CreateTicketPurchaseRequest request) {
        CreateTicketPurchaseResponse response = ticketPurchaseService.createTicketPurchase(request);
        return ResponseEntity.status(201).body(response);
    }

    @GetMapping
    public ResponseEntity<Page<TicketPurchaseResponse>> getAllTicketPurchases(
            @PageableDefault(size = 20, sort = "createdAt", direction = Direction.DESC)
            Pageable pageable) {
        return ResponseEntity.ok(ticketPurchaseService.getAllTicketPurchases(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TicketPurchaseResponse> getTicketPurchaseById(@PathVariable UUID id) {
        return ResponseEntity.ok(ticketPurchaseService.getTicketPurchaseById(id));
    }
}