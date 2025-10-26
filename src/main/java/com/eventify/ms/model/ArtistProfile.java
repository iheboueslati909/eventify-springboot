package com.eventify.ms.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import com.eventify.ms.enums.MusicGenre;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.*;

@Entity
@Table(name = "artist_profiles")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ArtistProfile {
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(columnDefinition = "uuid", updatable = false, nullable = false)
    private UUID id;

    // reference to owning member
    @Column(name = "member_id", nullable = false)
    private UUID memberId;

    @Column(name = "artist_name", nullable = false)
    private String artistName;

    @Column(name = "bio", columnDefinition = "text")
    private String bio;

    @Column(name = "is_deleted", nullable = false)
    private boolean isDeleted = false;

    @ElementCollection(targetClass = MusicGenre.class, fetch = FetchType.LAZY)
    @CollectionTable(name = "artist_profile_genres", joinColumns = @JoinColumn(name = "artist_profile_id"))
    @Column(name = "genre")
    @Enumerated(EnumType.STRING)
    private Set<MusicGenre> genres = new HashSet<>();

    @Column(name = "email")
    private String email;

    // flatten SocialMediaLinks into columns
    @Column(name = "social_instagram")
    private String socialInstagram;

    @Column(name = "social_facebook")
    private String socialFacebook;

    @Column(name = "social_twitter")
    private String socialTwitter;

    @Column(name = "social_soundcloud")
    private String socialSoundcloud;
}
