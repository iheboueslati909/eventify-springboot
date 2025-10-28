package com.eventify.ms.model;

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
class ConceptTest {
    @InjectMocks
    private Concept concept = Concept.builder()
            .id(UUID.randomUUID())
            .memberId(UUID.randomUUID())
            .title("Test Title")
            .description("Test Description")
            .isDeleted(false)
            .createdAt(OffsetDateTime.now())
            .genres(new HashSet<>())
            .build();

    @Test
    void shouldCreateConceptWithValidFields() {
        assertThat(concept.getId()).isNotNull();
        assertThat(concept.getMemberId()).isNotNull();
        assertThat(concept.getTitle()).isEqualTo("Test Title");
        assertThat(concept.getDescription()).isEqualTo("Test Description");
        assertThat(concept.isDeleted()).isFalse();
        assertThat(concept.getCreatedAt()).isNotNull();
        assertThat(concept.getGenres()).isEmpty();
    }

    @Test
    void shouldSetAndGetGenres() {
    Set<com.eventify.ms.enums.MusicGenre> genres = new HashSet<>();
    genres.add(com.eventify.ms.enums.MusicGenre.TECHNO);
    concept.setGenres(genres);
    assertThat(concept.getGenres()).containsExactly(com.eventify.ms.enums.MusicGenre.TECHNO);
    }

    @Test
    void shouldMarkConceptAsDeleted() {
        concept.setDeleted(true);
        assertThat(concept.isDeleted()).isTrue();
    }

    @Test
    void shouldHandleNullDescription() {
        concept.setDescription(null);
        assertThat(concept.getDescription()).isNull();
    }

    @Test
    void shouldHandleEmptyTitle() {
        concept.setTitle("");
        assertThat(concept.getTitle()).isEmpty();
    }
}
