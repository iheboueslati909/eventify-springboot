package com.eventify.ms.service;

import com.eventify.ms.dto.member.MemberResponse;
import com.eventify.ms.dto.member.UpdateMemberRequest;
import com.eventify.ms.model.Member;
import com.eventify.ms.model.ArtistProfile;
import com.eventify.ms.model.Ticket;
import com.eventify.ms.model.TicketPurchase;
import com.eventify.ms.model.auth.User;
import com.eventify.ms.repository.MemberRepository;
import com.eventify.ms.repository.auth.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class MemberService {

    private final MemberRepository memberRepository;
    private final UserRepository userRepository;

    public MemberService(MemberRepository memberRepository, UserRepository userRepository) {
        this.memberRepository = memberRepository;
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public Page<MemberResponse> getAllMembers(Pageable pageable) {
        Page<Member> members = memberRepository.findAllActiveWithUser(pageable);
        return members.map(this::mapToResponse);
    }

    @Transactional(readOnly = true)
    public MemberResponse getMemberById(UUID id) {
        Member member = memberRepository.findByIdWithRelationships(id)
                .orElseThrow(() -> new NoSuchElementException("Member not found with id: " + id));

        if (member.isDeleted()) {
            throw new IllegalStateException("Member is already deleted with id: " + id);
        }

        return mapToResponse(member);
    }

    @Transactional(readOnly = true)
    public MemberResponse getMemberByEmail(String email) {
        Member member = memberRepository.findByEmailWithRelationships(email)
                .orElseThrow(() -> new NoSuchElementException("Member not found with email: " + email));

        return mapToResponse(member);
    }

    @Transactional
    public MemberResponse updateMember(UUID id, UpdateMemberRequest request, java.util.UUID userId) {
        Member member = memberRepository.findByIdWithRelationships(id)
                .orElseThrow(() -> new NoSuchElementException("Member not found with id: " + id));

        if (member.isDeleted()) {
            throw new IllegalStateException("Member is already deleted with id: " + id);
        }

        // Check if new email is already in use by another member
        if (!member.getEmail().equals(request.email()) &&
            memberRepository.existsByEmailAndIsDeletedFalse(request.email())) {
            throw new IllegalStateException("Email already in use: " + request.email());
        }

        member.setFirstName(request.firstName());
        member.setLastName(request.lastName());
        member.setEmail(request.email());

        return mapToResponse(member);
    }

    @Transactional
    public MemberResponse updateMember(UUID id, UpdateMemberRequest request) {
        return updateMember(id, request, null);
    }

    @Transactional
    public void deleteMember(UUID id, java.util.UUID userId) {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Member not found with id: " + id));

        if (member.isDeleted()) {
            throw new IllegalStateException("Member is already deleted with id: " + id);
        }

        member.setDeleted(true);
    }

    @Transactional
    public void deleteMember(UUID id) {
        deleteMember(id, null);
    }

    private MemberResponse mapToResponse(Member member) {
        return new MemberResponse(
            member.getId(),
            member.getFirstName(),
            member.getLastName(),
            member.getEmail(),
            member.isDeleted(),
            member.getCreatedAt(),
            member.getUser().getId(),
            member.getArtistProfiles().stream()
                .map(ArtistProfile::getId)
                .collect(Collectors.toList()),
            member.getTickets().stream()
                .map(Ticket::getId)
                .collect(Collectors.toList()),
            member.getTicketPurchases().stream()
                .map(TicketPurchase::getId)
                .collect(Collectors.toList())
        );
    }
}