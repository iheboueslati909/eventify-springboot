package com.eventify.ms.controller;

import com.eventify.ms.dto.artist.CreateArtistProfileRequest;
import com.eventify.ms.dto.artist.ArtistProfileResponse;
import com.eventify.ms.dto.artist.UpdateArtistProfileRequest;
import com.eventify.ms.service.ArtistProfileService;
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
@RequestMapping("/api/artist-profiles")
public class ArtistProfileController {

    private final ArtistProfileService artistProfileService;

    public ArtistProfileController(ArtistProfileService artistProfileService) {
        this.artistProfileService = artistProfileService;
    }

    @PostMapping
    public ResponseEntity<?> createArtistProfile(@Valid @RequestBody CreateArtistProfileRequest request) {
        UUID id = artistProfileService.createArtistProfile(request);
        return ResponseEntity.status(201).body(Map.of("id", id));
    }

    @GetMapping
    public ResponseEntity<Page<ArtistProfileResponse>> getAllArtistProfiles(
            @PageableDefault(size = 20, sort = "createdAt", direction = Direction.DESC)
            Pageable pageable) {
        return ResponseEntity.ok(artistProfileService.getAllArtistProfiles(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ArtistProfileResponse> getArtistProfileById(@PathVariable UUID id) {
        return ResponseEntity.ok(artistProfileService.getArtistProfileById(id));
    }

    @GetMapping("/member/{memberId}")
    public ResponseEntity<ArtistProfileResponse> getArtistProfileByMemberId(@PathVariable UUID memberId) {
        return ResponseEntity.ok(artistProfileService.getArtistProfileByMemberId(memberId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ArtistProfileResponse> updateArtistProfile(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateArtistProfileRequest request) {
        return ResponseEntity.ok(artistProfileService.updateArtistProfile(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteArtistProfile(@PathVariable UUID id) {
        artistProfileService.deleteArtistProfile(id);
        return ResponseEntity.noContent().build();
    }
}