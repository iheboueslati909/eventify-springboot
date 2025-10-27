package com.eventify.ms.controller;

import com.eventify.ms.dto.event.CreateEventRequest;
import com.eventify.ms.model.Event;
import com.eventify.ms.service.EventService;
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

    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    @PostMapping
    public ResponseEntity<?> createEvent(@Valid @RequestBody CreateEventRequest request) {
        UUID eventId = eventService.createEvent(request);
        return ResponseEntity.status(201).body(Map.of("id", eventId));
    }

    @GetMapping
    public ResponseEntity<?> getAllEvents(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Page<Event> events = eventService.getAllEvents(PageRequest.of(page, size));
        return ResponseEntity.ok(events);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getEventById(@PathVariable UUID id) {
        Event event = eventService.getEventById(id);
        return ResponseEntity.ok(event);
    }

    @GetMapping("/concept/{conceptId}")
    public ResponseEntity<?> getEventsByConcept(@PathVariable UUID conceptId) {
        List<Event> events = eventService.getEventsByConcept(conceptId);
        return ResponseEntity.ok(events);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateEvent(@PathVariable UUID id, @Valid @RequestBody CreateEventRequest request) {
        UUID updatedId = eventService.updateEvent(id, request);
        return ResponseEntity.ok(Map.of("id", updatedId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteEvent(@PathVariable UUID id) {
        eventService.deleteEvent(id);
        return ResponseEntity.noContent().build();
    }
}