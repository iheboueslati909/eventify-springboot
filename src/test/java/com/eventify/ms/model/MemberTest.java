package com.eventify.ms.model;

import com.eventify.ms.model.auth.User;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.UUID;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class MemberTest {
    @InjectMocks
    private Member member = Member.builder()
            .id(UUID.randomUUID())
            .firstName("John")
            .lastName("Doe")
            .email("john.doe@email.com")
            .isDeleted(false)
            .createdAt(OffsetDateTime.now())
            .user(null)
            .artistProfiles(new ArrayList<>())
            .tickets(new ArrayList<>())
            .ticketPurchases(new ArrayList<>())
            .build();

    @Test
    void shouldCreateMemberWithValidFields() {
        assertThat(member.getId()).isNotNull();
        assertThat(member.getFirstName()).isEqualTo("John");
        assertThat(member.getLastName()).isEqualTo("Doe");
        assertThat(member.getEmail()).isEqualTo("john.doe@email.com");
        assertThat(member.isDeleted()).isFalse();
        assertThat(member.getCreatedAt()).isNotNull();
        assertThat(member.getUser()).isNull();
        assertThat(member.getArtistProfiles()).isEmpty();
        assertThat(member.getTickets()).isEmpty();
        assertThat(member.getTicketPurchases()).isEmpty();
    }

    @Test
    void shouldMarkMemberAsDeleted() {
        member.setDeleted(true);
        assertThat(member.isDeleted()).isTrue();
    }

    @Test
    void shouldHandleNullEmail() {
        member.setEmail(null);
        assertThat(member.getEmail()).isNull();
    }

    @Test
    void shouldHandleEmptyFirstName() {
        member.setFirstName("");
        assertThat(member.getFirstName()).isEmpty();
    }
}
