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
class ClubTest {
    @InjectMocks
    private Club club = Club.builder()
            .id(UUID.randomUUID())
            .name("Test Club")
            .address("123 Main St")
            .capacity(100)
            .isDeleted(false)
            .createdAt(OffsetDateTime.now())
            .owners(new HashSet<>())
            .build();

    @Test
    void shouldCreateClubWithValidFields() {
        assertThat(club.getId()).isNotNull();
        assertThat(club.getName()).isEqualTo("Test Club");
        assertThat(club.getAddress()).isEqualTo("123 Main St");
        assertThat(club.getCapacity()).isEqualTo(100);
        assertThat(club.isDeleted()).isFalse();
        assertThat(club.getCreatedAt()).isNotNull();
        assertThat(club.getOwners()).isEmpty();
    }

    @Test
    void shouldSetAndGetOwners() {
        Set<Member> owners = new HashSet<>();
        owners.add(Member.builder().id(UUID.randomUUID()).build());
        club.setOwners(owners);
        assertThat(club.getOwners()).hasSize(1);
    }

    @Test
    void shouldMarkClubAsDeleted() {
        club.setDeleted(true);
        assertThat(club.isDeleted()).isTrue();
    }

    @Test
    void shouldHandleNullAddress() {
        club.setAddress(null);
        assertThat(club.getAddress()).isNull();
    }

    @Test
    void shouldHandleEmptyName() {
        club.setName("");
        assertThat(club.getName()).isEmpty();
    }
}
