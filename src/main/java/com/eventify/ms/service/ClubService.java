package com.eventify.ms.service;

import com.eventify.ms.dto.club.*;
import com.eventify.ms.model.Club;
import com.eventify.ms.model.Member;
import com.eventify.ms.repository.ClubRepository;
import com.eventify.ms.repository.MemberRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    @Transactional
    public UUID createClub(CreateClubRequest request) {
        Set<UUID> ownerIds = request.ownerMemberIds();
        List<Member> owners = memberRepository.findAllById(ownerIds);
        
        if (owners.size() != ownerIds.size()) {
            Set<UUID> foundIds = owners.stream()
                .map(Member::getId)
                .collect(Collectors.toSet());
            Set<UUID> missingIds = new HashSet<>(ownerIds);
            missingIds.removeAll(foundIds);
            throw new IllegalArgumentException("Owner members not found: " + missingIds);
        }

        Club club = Club.builder()
                .name(request.name())
                .address(request.address())
                .capacity(request.capacity())
                .owners(new HashSet<>(owners))
                .build();

        club = clubRepository.save(club);
        return club.getId();
    }   

    @Transactional(readOnly = true)
    public Page<ClubResponse> getAllClubs(Pageable pageable) {
        // Use query with JOIN FETCH to avoid N+1
        Page<Club> clubs = clubRepository.findAllActiveWithOwners(pageable);
        return clubs.map(this::mapToResponse);
    }

    @Transactional(readOnly = true)
    public ClubResponse getClubById(UUID id) {
        // Single query with fetch join
        Club club = clubRepository.findByIdWithOwners(id)
                .orElseThrow(() -> new NoSuchElementException("Club not found with id: " + id));
        
        if (club.isDeleted()) {
            throw new IllegalStateException("Club is already deleted with id: " + id);
        }
        
        return mapToResponse(club);
    }

    @Transactional
    public ClubResponse updateClub(UUID id, UpdateClubRequest request) {
        Club club = clubRepository.findByIdWithOwners(id)
                .orElseThrow(() -> new NoSuchElementException("Club not found with id: " + id));

        if (club.isDeleted()) {
            throw new IllegalStateException("Club is already deleted with id: " + id);
        }

        // Verify owners if provided
        if (request.ownerMemberIds() != null && !request.ownerMemberIds().isEmpty()) {
            List<Member> owners = memberRepository.findAllById(request.ownerMemberIds());
            if (owners.size() != request.ownerMemberIds().size()) {
                throw new IllegalArgumentException("Some owner members not found");
            }
            club.setOwners(new HashSet<>(owners));
        }

        club.setName(request.name());
        club.setAddress(request.address());
        club.setCapacity(request.capacity());
        
        return mapToResponse(club);
    }

    @Transactional
    public void deleteClub(UUID id) {
        Club club = clubRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Club not found with id: " + id));
        
        if (club.isDeleted()) {
            throw new IllegalStateException("Club is already deleted with id: " + id);
        }
        
        club.setDeleted(true);
    }

    private ClubResponse mapToResponse(Club club) {
        // Owners already loaded via fetch join
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
