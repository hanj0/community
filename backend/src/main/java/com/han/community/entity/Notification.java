package com.han.community.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

/// isRead = false일때만 유니크라 DB에서 유니크 제약걸어야함
/// CREATE UNIQUE INDEX uk_active_group
/// ON notification (recipient_id, type, target_type, target_id)
/// WHERE is_read = false;
@Entity
@Getter
@NoArgsConstructor
public class Notification extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long recipientId;
    @Enumerated(EnumType.STRING)
    private TargetType targetType;
    private Long targetId;
    private Long rootPostId;
    @Enumerated(EnumType.STRING)
    private NotificationType type;
    private String contentPreview;
    private int actorCount;
    private Long lastActorId;
    private boolean isRead;

    public static Notification create(Long actorId, Long recipientId, TargetType targetType, Long targetId, Long rootPostId, NotificationType type, String contentPreview) {
        Notification n = new Notification();
        n.recipientId = recipientId;
        n.targetType = targetType;
        n.targetId = targetId;
        n.rootPostId = rootPostId;
        n.type = type;
        n.contentPreview = contentPreview;
        n.actorCount = 1;
        n.lastActorId = actorId;
        n.isRead = false;
        return n;
    }
}
