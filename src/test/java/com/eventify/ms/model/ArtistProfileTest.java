package com.eventify.ms.model;

import com.eventify.ms.enums.MusicGenre;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;
import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class ArtistProfileTest {
    @InjectMocks
    private ArtistProfile artistProfile = ArtistProfile.builder()
            .id(UUID.randomUUID())
            .memberId(UUID.randomUUID())
            .artistName("Test Artist")
            .bio("Test Bio")
            .isDeleted(false)
            .createdAt(OffsetDateTime.now())
            .genres(new HashSet<>())
            .email("artist@email.com")
            .socialInstagram("insta")
            .socialFacebook("fb")
            .socialTwitter("tw")
            .build();

    @Test
    void shouldCreateArtistProfileWithValidFields() {
        assertThat(artistProfile.getId()).isNotNull();
        assertThat(artistProfile.getMemberId()).isNotNull();
        assertThat(artistProfile.getArtistName()).isEqualTo("Test Artist");
        assertThat(artistProfile.getBio()).isEqualTo("Test Bio");
        assertThat(artistProfile.isDeleted()).isFalse();
        assertThat(artistProfile.getCreatedAt()).isNotNull();
        assertThat(artistProfile.getGenres()).isEmpty();
        assertThat(artistProfile.getEmail()).isEqualTo("artist@email.com");
        assertThat(artistProfile.getSocialInstagram()).isEqualTo("insta");
        assertThat(artistProfile.getSocialFacebook()).isEqualTo("fb");
        assertThat(artistProfile.getSocialTwitter()).isEqualTo("tw");
    }

    @Test
    void shouldSetAndGetGenres() {
        Set<MusicGenre> genres = new HashSet<>();
        genres.add(MusicGenre.TECHNO);
        artistProfile.setGenres(genres);
        assertThat(artistProfile.getGenres()).containsExactly(MusicGenre.TECHNO);
    }

    @Test
    void shouldMarkArtistProfileAsDeleted() {
        artistProfile.setDeleted(true);
        assertThat(artistProfile.isDeleted()).isTrue();
    }

    @Test
    void shouldHandleNullBio() {
        artistProfile.setBio(null);
        assertThat(artistProfile.getBio()).isNull();
    }

    @Test
    void shouldHandleEmptyArtistName() {
        artistProfile.setArtistName("");
        assertThat(artistProfile.getArtistName()).isEmpty();
    }
}
