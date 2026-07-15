package com.han.community.dto;

import com.han.community.entity.Notification;
import com.han.community.entity.NotificationType;
import com.han.community.entity.TargetType;

import java.time.LocalDateTime;

public class NotificationDto {

    public record UnreadCountResponse(long unreadCount) {}

    public record Response(
            Long id,
            NotificationType type,
            TargetType targetType,
            Long targetId,
            Long rootPostId,
            String lastActorName,
            long actorCount,
            boolean isRead,
            LocalDateTime updatedAt
    ) {
        public static Response from(Notification n, String lastActorName) {
            return new Response(
                    n.getId(),
                    n.getType(),
                    n.getTargetType(),
                    n.getTargetId(),
                    n.getRootPostId(),
                    lastActorName,
                    n.getActorCount(),
                    n.isRead(),
                    n.getUpdatedAt()
            );
        }
    }
}
