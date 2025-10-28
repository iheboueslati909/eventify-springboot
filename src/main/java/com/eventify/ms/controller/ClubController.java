package com.eventify.ms.controller;

import com.eventify.ms.dto.club.*;
import com.eventify.ms.service.ClubService;
import jakarta.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/clubs")
public class ClubController {

    private final ClubService clubService;

    public ClubController(ClubService clubService) {
        this.clubService = clubService;
    }

    @PostMapping
    public ResponseEntity<Map<String, UUID>> createClub(@Valid @RequestBody CreateClubRequest request) {
        UUID clubId = clubService.createClub(request);
        return ResponseEntity.status(201).body(Map.of("id", clubId));
    }

    @GetMapping
    public ResponseEntity<Page<ClubResponse>> getAllClubs(
            @PageableDefault(size = 20, sort = "createdAt", direction = Direction.DESC) 
            Pageable pageable) {
        return ResponseEntity.ok(clubService.getAllClubs(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClubResponse> getClubById(@PathVariable UUID id) {
        return ResponseEntity.ok(clubService.getClubById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ClubResponse> updateClub(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateClubRequest request
    ) {
        return ResponseEntity.ok(clubService.updateClub(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteClub(@PathVariable UUID id) {
        clubService.deleteClub(id);
        return ResponseEntity.noContent().build();
    }
}
