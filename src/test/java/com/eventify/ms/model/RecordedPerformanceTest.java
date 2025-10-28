package com.eventify.ms.model;

import com.eventify.ms.enums.PerformanceType;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;
import java.time.OffsetDateTime;
import java.util.UUID;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class RecordedPerformanceTest {
    @InjectMocks
    private RecordedPerformance recordedPerformance = RecordedPerformance.builder()
        .id(UUID.randomUUID())
        .mediaUrl("http://media.url")
        .type(PerformanceType.LISE_SET)
        .createdAt(OffsetDateTime.now())
        .lastModified(OffsetDateTime.now())
        .build();

    @Test
    void shouldCreateRecordedPerformanceWithValidFields() {
    assertThat(recordedPerformance.getId()).isNotNull();
    assertThat(recordedPerformance.getMediaUrl()).isEqualTo("http://media.url");
    assertThat(recordedPerformance.getType()).isEqualTo(PerformanceType.LISE_SET);
    assertThat(recordedPerformance.getCreatedAt()).isNotNull();
    assertThat(recordedPerformance.getLastModified()).isNotNull();
    }

    @Test
    void shouldHandleNullMediaUrl() {
        recordedPerformance.setMediaUrl(null);
        assertThat(recordedPerformance.getMediaUrl()).isNull();
    }

    @Test
    void shouldChangePerformanceType() {
    recordedPerformance.setType(PerformanceType.DJ_SET);
    assertThat(recordedPerformance.getType()).isEqualTo(PerformanceType.DJ_SET);
    }
}
