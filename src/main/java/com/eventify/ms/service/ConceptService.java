package com.eventify.ms.service;

import com.eventify.ms.dto.concept.CreateConceptRequest;
import com.eventify.ms.dto.concept.ConceptResponse;
import com.eventify.ms.dto.concept.UpdateConceptRequest;
import com.eventify.ms.model.Concept;
import com.eventify.ms.model.Member;
import com.eventify.ms.repository.ConceptRepository;
import com.eventify.ms.repository.MemberRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;
import java.util.Set;
import java.util.UUID;

@Service
public class ConceptService {

    private final ConceptRepository conceptRepository;
    private final MemberRepository memberRepository;

    public ConceptService(ConceptRepository conceptRepository, MemberRepository memberRepository) {
        this.conceptRepository = conceptRepository;
        this.memberRepository = memberRepository;
    }

    @Transactional
    public UUID createConcept(CreateConceptRequest request) {
        UUID memberId = request.memberId();
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new NoSuchElementException("Member not found with id: " + memberId));

        Concept concept = Concept.builder()
                .memberId(member.getId())
                .title(request.title())
                .description(request.description())
                .genres(request.genres() == null ? Set.of() : request.genres())
                .build();

        concept = conceptRepository.save(concept);
        return concept.getId();
    }

    @Transactional(readOnly = true)
    public Page<ConceptResponse> getAllConcepts(Pageable pageable) {
        Page<Concept> concepts = conceptRepository.findAllActiveWithGenres(pageable);
        return concepts.map(this::mapToResponse);
    }

    @Transactional(readOnly = true)
    public ConceptResponse getConceptById(UUID id) {
        Concept concept = conceptRepository.findByIdWithGenres(id)
                .orElseThrow(() -> new NoSuchElementException("Concept not found with id: " + id));

        if (concept.isDeleted()) {
            throw new IllegalStateException("Concept is already deleted with id: " + id);
        }

        return mapToResponse(concept);
    }

    @Transactional
    public ConceptResponse updateConcept(UUID id, UpdateConceptRequest request) {
        Concept concept = conceptRepository.findByIdWithGenres(id)
                .orElseThrow(() -> new NoSuchElementException("Concept not found with id: " + id));

        if (concept.isDeleted()) {
            throw new IllegalStateException("Concept is already deleted with id: " + id);
        }

        concept.setTitle(request.title());
        concept.setDescription(request.description());
        concept.setGenres(request.genres() == null ? Set.of() : request.genres());

        return mapToResponse(concept);
    }

    @Transactional
    public void deleteConcept(UUID id) {
        Concept concept = conceptRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Concept not found with id: " + id));

        if (concept.isDeleted()) {
            throw new IllegalStateException("Concept is already deleted with id: " + id);
        }

        concept.setDeleted(true);
    }

    private ConceptResponse mapToResponse(Concept concept) {
        return new ConceptResponse(
                concept.getId(),
                concept.getMemberId(),
                concept.getTitle(),
                concept.getDescription(),
                concept.isDeleted(),
                concept.getCreatedAt(),
                concept.getGenres()
        );
    }
}
