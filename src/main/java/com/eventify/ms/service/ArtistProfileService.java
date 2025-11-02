package com.eventify.ms.service;

import com.eventify.ms.dto.artist.CreateArtistProfileRequest;
import com.eventify.ms.dto.artist.ArtistProfileResponse;
import com.eventify.ms.dto.artist.UpdateArtistProfileRequest;
import com.eventify.ms.model.ArtistProfile;
import com.eventify.ms.model.Member;
import com.eventify.ms.repository.ArtistProfileRepository;
import com.eventify.ms.repository.MemberRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;
import java.util.Set;
import java.util.UUID;

@Service
public class ArtistProfileService {

    private final ArtistProfileRepository artistProfileRepository;
    private final MemberRepository memberRepository;

    public ArtistProfileService(ArtistProfileRepository artistProfileRepository, MemberRepository memberRepository) {
        this.artistProfileRepository = artistProfileRepository;
        this.memberRepository = memberRepository;
    }

    @Transactional
    public UUID createArtistProfile(CreateArtistProfileRequest request) {
        UUID memberId = request.memberId();
        
        // Check if member exists
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new NoSuchElementException("Member not found with id: " + memberId));
                
        // Check if member already has an artist profile
        if (artistProfileRepository.findByMemberIdAndIsDeletedFalse(memberId).isPresent()) {
            throw new IllegalStateException("Member already has an active artist profile");
        }

        ArtistProfile artistProfile = ArtistProfile.builder()
                .memberId(member.getId())
                .artistName(request.artistName())
                .bio(request.bio())
                .genres(request.genres() == null ? Set.of() : request.genres())
                .email(request.email())
                .socialInstagram(request.socialInstagram())
                .socialFacebook(request.socialFacebook())
                .socialTwitter(request.socialTwitter())
                .socialSoundcloud(request.socialSoundcloud())
                .build();

        artistProfile = artistProfileRepository.save(artistProfile);
        return artistProfile.getId();
    }

    @Transactional(readOnly = true)
    public Page<ArtistProfileResponse> getAllArtistProfiles(Pageable pageable) {
        Page<ArtistProfile> profiles = artistProfileRepository.findAllActiveWithGenres(pageable);
        return profiles.map(this::mapToResponse);
    }

    @Transactional(readOnly = true)
    public ArtistProfileResponse getArtistProfileById(UUID id) {
        ArtistProfile profile = artistProfileRepository.findByIdWithGenres(id)
                .orElseThrow(() -> new NoSuchElementException("Artist profile not found with id: " + id));

        if (profile.isDeleted()) {
            throw new IllegalStateException("Artist profile is already deleted with id: " + id);
        }

        return mapToResponse(profile);
    }

    @Transactional(readOnly = true)
    public ArtistProfileResponse getArtistProfileByMemberId(UUID memberId) {
        ArtistProfile profile = artistProfileRepository.findByMemberIdAndIsDeletedFalse(memberId)
                .orElseThrow(() -> new NoSuchElementException("Artist profile not found for member id: " + memberId));

        return mapToResponse(profile);
    }

    @Transactional
    public ArtistProfileResponse updateArtistProfile(UUID id, UpdateArtistProfileRequest request, java.util.UUID userId) {
        ArtistProfile profile = artistProfileRepository.findByIdWithGenres(id)
                .orElseThrow(() -> new NoSuchElementException("Artist profile not found with id: " + id));

        if (profile.isDeleted()) {
            throw new IllegalStateException("Artist profile is already deleted with id: " + id);
        }

        profile.setArtistName(request.artistName());
        profile.setBio(request.bio());
        profile.setGenres(request.genres() == null ? Set.of() : request.genres());
        profile.setEmail(request.email());
        profile.setSocialInstagram(request.socialInstagram());
        profile.setSocialFacebook(request.socialFacebook());
        profile.setSocialTwitter(request.socialTwitter());
        profile.setSocialSoundcloud(request.socialSoundcloud());

        return mapToResponse(profile);
    }

    // Backwards-compatible overloads (keep existing callers working)
    @Transactional
    public ArtistProfileResponse updateArtistProfile(UUID id, UpdateArtistProfileRequest request) {
        return updateArtistProfile(id, request, null);
    }

    @Transactional
    public void deleteArtistProfile(UUID id, java.util.UUID userId) {
        ArtistProfile profile = artistProfileRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Artist profile not found with id: " + id));

        if (profile.isDeleted()) {
            throw new IllegalStateException("Artist profile is already deleted with id: " + id);
        }

        profile.setDeleted(true);
    }

    @Transactional
    public void deleteArtistProfile(UUID id) {
        deleteArtistProfile(id, null);
    }

    private ArtistProfileResponse mapToResponse(ArtistProfile profile) {
        return new ArtistProfileResponse(
                profile.getId(),
                profile.getMemberId(),
                profile.getArtistName(),
                profile.getBio(),
                profile.isDeleted(),
                profile.getGenres(),
                profile.getEmail(),
                profile.getSocialInstagram(),
                profile.getSocialFacebook(),
                profile.getSocialTwitter(),
                profile.getSocialSoundcloud()
        );
    }
}