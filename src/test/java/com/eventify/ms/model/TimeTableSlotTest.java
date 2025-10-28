package com.eventify.ms.model;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;
import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.UUID;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class TimeTableSlotTest {
    @InjectMocks
    private TimeTableSlot slot = TimeTableSlot.builder()
            .id(UUID.randomUUID())
            .startTime(OffsetDateTime.now())
            .endTime(OffsetDateTime.now().plusHours(1))
            .title("Slot Title")
            .createdAt(OffsetDateTime.now())
            .artistProfiles(new HashSet<>())
            .timetable(null)
            .build();

    @Test
    void shouldCreateTimeTableSlotWithValidFields() {
        assertThat(slot.getId()).isNotNull();
        assertThat(slot.getStartTime()).isNotNull();
        assertThat(slot.getEndTime()).isNotNull();
        assertThat(slot.getTitle()).isEqualTo("Slot Title");
        assertThat(slot.getCreatedAt()).isNotNull();
        assertThat(slot.getArtistProfiles()).isEmpty();
        assertThat(slot.getTimetable()).isNull();
    }

    @Test
    void shouldHandleNullTitle() {
        slot.setTitle(null);
        assertThat(slot.getTitle()).isNull();
    }

    @Test
    void shouldAddArtistProfile() {
        ArtistProfile artist = ArtistProfile.builder().id(UUID.randomUUID()).build();
        slot.getArtistProfiles().add(artist);
        assertThat(slot.getArtistProfiles()).hasSize(1);
    }
}
