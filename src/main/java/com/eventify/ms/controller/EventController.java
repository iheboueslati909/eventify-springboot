package com.eventify.ms.controller;

import com.eventify.ms.dto.event.CreateEventRequest;
import com.eventify.ms.dto.event.EventResponse;
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
    public ResponseEntity<Map<String, UUID>> createEvent(@Valid @RequestBody CreateEventRequest request) {
        UUID eventId = eventService.createEvent(request);
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
    public ResponseEntity<Map<String, UUID>> updateEvent(@PathVariable UUID id, @Valid @RequestBody CreateEventRequest request) {
        UUID updatedId = eventService.updateEvent(id, request);
        return ResponseEntity.ok(Map.of("id", updatedId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEvent(@PathVariable UUID id) {
        eventService.deleteEvent(id);
        return ResponseEntity.noContent().build();
    }


}