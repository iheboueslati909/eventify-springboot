package com.eventify.ms.controller;

import com.eventify.ms.dto.concept.CreateConceptRequest;
import com.eventify.ms.dto.concept.ConceptResponse;
import com.eventify.ms.dto.concept.UpdateConceptRequest;
import com.eventify.ms.service.ConceptService;
import com.eventify.ms.service.auth.JwtService;
import com.eventify.ms.exception.InvalidTokenException;
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
@RequestMapping("/api/concepts")
public class ConceptController {

    private final ConceptService conceptService;
    private final JwtService jwtService;

    public ConceptController(ConceptService conceptService, JwtService jwtService) {
        this.conceptService = conceptService;
        this.jwtService = jwtService;
    }

    @PostMapping
    public ResponseEntity<Map<String, UUID>> createConcept(@Valid @RequestBody CreateConceptRequest request) {
        UUID id = conceptService.createConcept(request);
        return ResponseEntity.status(201).body(Map.of("id", id));
    }

    @GetMapping
    public ResponseEntity<Page<ConceptResponse>> getAllConcepts(
            @PageableDefault(size = 20, sort = "createdAt", direction = Direction.DESC)
            Pageable pageable) {
        return ResponseEntity.ok(conceptService.getAllConcepts(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ConceptResponse> getConceptById(@PathVariable UUID id) {
        return ResponseEntity.ok(conceptService.getConceptById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ConceptResponse> updateConcept(@PathVariable UUID id, @Valid @RequestBody UpdateConceptRequest request,
                                                          @RequestHeader("Authorization") String authHeader) {
        String token = jwtService.extractTokenFromString(authHeader);
        UUID userId = jwtService.extractUserId(token).orElseThrow(() -> new InvalidTokenException("Invalid token"));
        return ResponseEntity.ok(conceptService.updateConcept(id, request, userId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteConcept(@PathVariable UUID id, @RequestHeader("Authorization") String authHeader) {
        String token = jwtService.extractTokenFromString(authHeader);
        UUID userId = jwtService.extractUserId(token).orElseThrow(() -> new InvalidTokenException("Invalid token"));
        conceptService.deleteConcept(id, userId);
        return ResponseEntity.noContent().build();
    }
}
