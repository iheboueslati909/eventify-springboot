package com.eventify.ms.model;

import com.eventify.ms.enums.EventStatus;
import com.eventify.ms.enums.EventType;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;
import java.time.OffsetDateTime;
import java.util.UUID;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class EventTest {
    @InjectMocks
    private Event event = Event.builder()
            .id(UUID.randomUUID())
            .title("Test Event")
            .description("Test Description")
            .startDate(OffsetDateTime.now())
            .endDate(OffsetDateTime.now().plusHours(2))
            .location("Venue")
            .type(EventType.CONCERT)
            .status(EventStatus.DRAFT)
            .conceptId(UUID.randomUUID())
            .isDeleted(false)
            .createdAt(OffsetDateTime.now())
            .clubId(UUID.randomUUID())
            .build();

    @Test
    void shouldCreateEventWithValidFields() {
        assertThat(event.getId()).isNotNull();
        assertThat(event.getTitle()).isEqualTo("Test Event");
        assertThat(event.getDescription()).isEqualTo("Test Description");
        assertThat(event.getStartDate()).isNotNull();
        assertThat(event.getEndDate()).isNotNull();
        assertThat(event.getLocation()).isEqualTo("Venue");
        assertThat(event.getType()).isEqualTo(EventType.CONCERT);
        assertThat(event.getStatus()).isEqualTo(EventStatus.DRAFT);
        assertThat(event.getConceptId()).isNotNull();
        assertThat(event.isDeleted()).isFalse();
        assertThat(event.getCreatedAt()).isNotNull();
        assertThat(event.getClubId()).isNotNull();
    }

    @Test
    void shouldMarkEventAsDeleted() {
        event.setDeleted(true);
        assertThat(event.isDeleted()).isTrue();
    }

    @Test
    void shouldHandleNullDescription() {
        event.setDescription(null);
        assertThat(event.getDescription()).isNull();
    }

    @Test
    void shouldHandleEmptyTitle() {
        event.setTitle("");
        assertThat(event.getTitle()).isEmpty();
    }
}
