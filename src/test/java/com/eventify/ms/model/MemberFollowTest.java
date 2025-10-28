package com.eventify.ms.model;

import com.eventify.ms.enums.FollowNotificationType;
import com.eventify.ms.enums.FollowTargetType;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;
import java.time.OffsetDateTime;
import java.util.UUID;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class MemberFollowTest {
    @InjectMocks
    private MemberFollow memberFollow = MemberFollow.builder()
            .id(UUID.randomUUID())
            .memberId(UUID.randomUUID())
            .targetId(UUID.randomUUID())
            .targetType(FollowTargetType.ARTIST)
            .createdAt(OffsetDateTime.now())
            .isMuted(false)
            .notificationType(FollowNotificationType.ALL)
            .build();

    @Test
    void shouldCreateMemberFollowWithValidFields() {
        assertThat(memberFollow.getId()).isNotNull();
        assertThat(memberFollow.getMemberId()).isNotNull();
        assertThat(memberFollow.getTargetId()).isNotNull();
        assertThat(memberFollow.getTargetType()).isEqualTo(FollowTargetType.ARTIST);
        assertThat(memberFollow.getCreatedAt()).isNotNull();
        assertThat(memberFollow.isMuted()).isFalse();
        assertThat(memberFollow.getNotificationType()).isEqualTo(FollowNotificationType.ALL);
    }

    @Test
    void shouldMarkMemberFollowAsMuted() {
        memberFollow.setMuted(true);
        assertThat(memberFollow.isMuted()).isTrue();
    }

    @Test
    void shouldChangeNotificationType() {
        memberFollow.setNotificationType(FollowNotificationType.NONE);
        assertThat(memberFollow.getNotificationType()).isEqualTo(FollowNotificationType.NONE);
    }
}
