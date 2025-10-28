package com.eventify.ms.model;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.UUID;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class TimeTableTest {
    @InjectMocks
    private TimeTable timeTable = TimeTable.builder()
            .id(UUID.randomUUID())
            .stageName("Main Stage")
            .createdAt(OffsetDateTime.now())
            .slots(new ArrayList<>())
            .event(null)
            .build();

    @Test
    void shouldCreateTimeTableWithValidFields() {
        assertThat(timeTable.getId()).isNotNull();
        assertThat(timeTable.getStageName()).isEqualTo("Main Stage");
        assertThat(timeTable.getCreatedAt()).isNotNull();
        assertThat(timeTable.getSlots()).isEmpty();
        assertThat(timeTable.getEvent()).isNull();
    }

    @Test
    void shouldHandleNullStageName() {
        timeTable.setStageName(null);
        assertThat(timeTable.getStageName()).isNull();
    }

    @Test
    void shouldAddSlot() {
        TimeTableSlot slot = TimeTableSlot.builder().id(UUID.randomUUID()).build();
        timeTable.getSlots().add(slot);
        assertThat(timeTable.getSlots()).hasSize(1);
    }
}
