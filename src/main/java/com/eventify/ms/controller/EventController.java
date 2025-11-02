package com.eventify.ms.controller;

import com.eventify.ms.dto.event.CreateEventRequest;
import com.eventify.ms.dto.event.EventResponse;
import com.eventify.ms.service.EventService;
import com.eventify.ms.service.auth.JwtService;
import com.eventify.ms.exception.InvalidTokenException;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/events")
public class EventController {

    private final EventService eventService;
    private final JwtService jwtService;

    public EventController(EventService eventService, JwtService jwtService) {
        this.eventService = eventService;
        this.jwtService = jwtService;
    }

    @PostMapping
    public ResponseEntity<Map<String, UUID>> createEvent(@Valid @RequestBody CreateEventRequest request
        , @RequestHeader("Authorization") String authHeader) {
        String token = jwtService.extractTokenFromString(authHeader);
        UUID userId = jwtService.extractUserId(token).orElseThrow(() -> new InvalidTokenException("Invalid token"));
        UUID eventId = eventService.createEvent(request, userId);
        return ResponseEntity.status(201).body(Map.of("id", eventId));
    }

    @GetMapping
    public ResponseEntity<Page<EventResponse>> getAllEvents(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Page<EventResponse> events = eventService.getAllEvents(PageRequest.of(page, size));
        return ResponseEntity.ok(events);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EventResponse> getEventById(@PathVariable UUID id) {
        EventResponse event = eventService.getEventById(id);
        return ResponseEntity.ok(event);
    }

    @GetMapping("/concept/{conceptId}")
    public ResponseEntity<List<EventResponse>> getEventsByConcept(@PathVariable UUID conceptId) {
        List<EventResponse> events = eventService.getEventsByConcept(conceptId);
        return ResponseEntity.ok(events);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Map<String, UUID>> updateEvent(@PathVariable UUID id, @Valid @RequestBody CreateEventRequest request,
                                                         @RequestHeader("Authorization") String authHeader) {
        String token = jwtService.extractTokenFromString(authHeader);
        UUID userId = jwtService.extractUserId(token).orElseThrow(() -> new InvalidTokenException("Invalid token"));
        UUID updatedId = eventService.updateEvent(id, request, userId);
        return ResponseEntity.ok(Map.of("id", updatedId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEvent(@PathVariable UUID id, @RequestHeader("Authorization") String authHeader) {
        String token = jwtService.extractTokenFromString(authHeader);
        UUID userId = jwtService.extractUserId(token).orElseThrow(() -> new InvalidTokenException("Invalid token"));
        eventService.deleteEvent(id, userId);
        return ResponseEntity.noContent().build();
    }
    
    @PostMapping("/{id}/cancel")
    public ResponseEntity<Void> cancelEvent(@PathVariable UUID id, @RequestHeader("Authorization") String authHeader) {
        String token = authHeader != null && authHeader.startsWith("Bearer ") ? authHeader.substring(7) : authHeader;
        UUID userId = jwtService.extractUserId(token).orElseThrow(() -> new InvalidTokenException("Invalid token"));
        eventService.cancelEvent(id, userId);
        return ResponseEntity.noContent().build();
    }


}