package com.eventify.ms.service;

import com.eventify.ms.dto.club.*;
import com.eventify.ms.model.Club;
import com.eventify.ms.model.Member;
import com.eventify.ms.repository.ClubRepository;
import com.eventify.ms.repository.MemberRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ClubService {

    private final ClubRepository clubRepository;
    private final MemberRepository memberRepository;

    public ClubService(ClubRepository clubRepository, MemberRepository memberRepository) {
        this.clubRepository = clubRepository;
        this.memberRepository = memberRepository;
    }

    // CREATE
    @Transactional
    public UUID createClub(CreateClubRequest request) {
        validateClubRequest(request.name(), request.address(), request.capacity(), request.ownerMemberIds());

        List<Member> owners = memberRepository.findAllById(request.ownerMemberIds());
        if (owners.isEmpty()) throw new IllegalArgumentException("No valid owner members found");

        Club club = Club.builder()
                .name(request.name())
                .address(request.address())
                .capacity(request.capacity())
                .isDeleted(false)
                .owners(new HashSet<>(owners))
                .build();

        clubRepository.save(club);
        return club.getId();
    }

    // READ ALL
    @Transactional(readOnly = true)
    public List<ClubResponse> getAllClubs() {
        return clubRepository.findAll().stream()
                .filter(c -> !c.isDeleted())
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // READ BY ID
    @Transactional(readOnly = true)
    public ClubResponse getClubById(UUID id) {
        Club club = clubRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Club not found"));
        if (club.isDeleted()) throw new IllegalArgumentException("Club is deleted");
        return mapToResponse(club);
    }

    // UPDATE
    @Transactional
    public ClubResponse updateClub(UUID id, UpdateClubRequest request) {
        Club club = clubRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Club not found"));

        validateClubRequest(request.name(), request.address(), request.capacity(), request.ownerMemberIds());

        List<Member> owners = request.ownerMemberIds() != null && !request.ownerMemberIds().isEmpty()
                ? memberRepository.findAllById(request.ownerMemberIds())
                : new ArrayList<>(club.getOwners());

        club.setName(request.name());
        club.setAddress(request.address());
        club.setCapacity(request.capacity());
        club.setOwners(new HashSet<>(owners));

        clubRepository.save(club);
        return mapToResponse(club);
    }

    // DELETE (soft delete)
    @Transactional
    public void deleteClub(UUID id) {
        Club club = clubRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Club not found"));
        club.setDeleted(true);
        clubRepository.save(club);
    }

    // --- Helpers ---
    private void validateClubRequest(String name, String address, Integer capacity, Set<UUID> owners) {
        if (name == null || name.isBlank())
            throw new IllegalArgumentException("Club name is required");
        if (address == null || address.isBlank())
            throw new IllegalArgumentException("Club address is required");
        if (capacity == null || capacity <= 0)
            throw new IllegalArgumentException("Capacity must be positive");
        if (owners == null || owners.isEmpty())
            throw new IllegalArgumentException("At least one owner is required");
    }

    private ClubResponse mapToResponse(Club club) {
        Set<UUID> ownerIds = club.getOwners().stream()
                .map(Member::getId)
                .collect(Collectors.toSet());
        return new ClubResponse(
                club.getId(),
                club.getName(),
                club.getAddress(),
                club.getCapacity(),
                club.isDeleted(),
                ownerIds
        );
    }
}
