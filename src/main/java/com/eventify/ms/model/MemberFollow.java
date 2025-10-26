package com.eventify.ms.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import com.eventify.ms.enums.FollowNotificationType;
import com.eventify.ms.enums.FollowTargetType;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "member_follows")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class MemberFollow {
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(columnDefinition = "uuid", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "member_id", nullable = false)
    private UUID memberId;

    @Column(name = "target_id", nullable = false)
    private UUID targetId;

    @Enumerated(EnumType.STRING)
    @Column(name = "target_type", nullable = false)
    private FollowTargetType targetType;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    @Column(name = "is_muted", nullable = false)
    private boolean isMuted = false;

    @Enumerated(EnumType.STRING)
    @Column(name = "notification_type", nullable = false)
    private FollowNotificationType notificationType;
}
