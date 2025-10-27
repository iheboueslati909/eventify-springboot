package com.eventify.ms.controller;

import com.eventify.ms.dto.event.CreateEventRequest;
import com.eventify.ms.service.EventService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
}