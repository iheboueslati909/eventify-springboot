package com.eventify.ms.controller;

import com.eventify.ms.dto.concept.CreateConceptRequest;
import com.eventify.ms.dto.concept.ConceptResponse;
import com.eventify.ms.dto.concept.UpdateConceptRequest;
import com.eventify.ms.service.ConceptService;
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

    public ConceptController(ConceptService conceptService) {
        this.conceptService = conceptService;
    }

    @PostMapping
    public ResponseEntity<?> createConcept(@Valid @RequestBody CreateConceptRequest request) {
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
    public ResponseEntity<ConceptResponse> updateConcept(@PathVariable UUID id, @Valid @RequestBody UpdateConceptRequest request) {
        return ResponseEntity.ok(conceptService.updateConcept(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteConcept(@PathVariable UUID id) {
        conceptService.deleteConcept(id);
        return ResponseEntity.noContent().build();
    }
}
