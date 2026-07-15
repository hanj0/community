package com.han.community.service;

import com.han.community.dto.NotificationDto;
import com.han.community.dto.common.NotificationCursor;
import com.han.community.entity.Notification;
import com.han.community.entity.NotificationType;
import com.han.community.entity.TargetType;
import com.han.community.event.ReactionEvent;
import com.han.community.dto.common.CursorResponse;
import com.han.community.repository.NotificationActorRepository;
import com.han.community.repository.NotificationRepository;
import com.han.community.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final NotificationActorRepository notificationActorRepository;
    private final UserRepository userRepository;

    private static final int NOTIFICATION_SIZE = 10;

    @Transactional
    public NotificationDto.UnreadCountResponse getUnreadNotificationCount(Long recipientId) {

        long count = notificationRepository.countByRecipientIdAndIsReadFalse(recipientId);

        return new NotificationDto.UnreadCountResponse(count);
    }

    @Transactional
    public CursorResponse<NotificationDto.Response> getNotifications(Long recipientId, String cursor) {

        int size = NOTIFICATION_SIZE;
        NotificationCursor c = NotificationCursor.parse(cursor);

        List<Notification> notifications = notificationRepository.findByCursor(
                recipientId,
                c == null ? null : c.updatedAt(),
                c == null ? null : c.id(),
                PageRequest.of(0, size + 1)
        );

        boolean hasNext = notifications.size() > size;
        List<Notification> page = hasNext ? notifications.subList(0, size) : notifications;

        List<Long> actorIds = page.stream()
                .map(n -> n.getLastActorId()).toList();
        Map<Long, String> names = userRepository.findAllById(actorIds).stream()
                .collect(Collectors.toMap(user -> user.getId(), user -> user.getUsername()));

        List<NotificationDto.Response> data = page.stream()
                .map(n -> NotificationDto.Response.from(n, names.get(n.getLastActorId()))).toList();

        Notification n = page.getLast();
        String nextCursor = hasNext ? new NotificationCursor(n.getUpdatedAt(), n.getId()).encode() : null;

        return new CursorResponse<>(data, nextCursor,hasNext);
    }

    @Transactional
    public void markRead(Long recipientId, Long notificationId) {

        notificationRepository.markRead(recipientId, notificationId);
    }

    @Transactional
    public void markAllRead(Long recipientId) {

        notificationRepository.markAllRead(recipientId);
    }

    @Transactional
    public void upsert(ReactionEvent event) {

        Long recipientId = event.recipientId();
        TargetType targetType = event.targetType();
        Long targetId = event.targetId();
        Long actorId = event.actorId();
        Long rootPostId = event.rootPostId();

        Optional<Notification> notification = notificationRepository.findActiveGroup(
                recipientId, targetType, targetId, NotificationType.REACTION
        );

        if(notification.isEmpty()) createNewGroup(actorId, recipientId, targetType, targetId, rootPostId);
        else updateGroup(notification.get().getId(), actorId);
    }

    /// DataIntegrityViolationException 발생가능
    private void createNewGroup(Long actorId, Long recipientId, TargetType targetType, Long targetId, Long rootPostId) {

        Notification notification = notificationRepository.save(
                Notification.create(
                        actorId,
                        recipientId,
                        targetType,
                        targetId,
                        rootPostId,
                        NotificationType.REACTION
                )
        );
        notificationActorRepository.InsertIgnore(notification.getId(), actorId);
    }

    private void updateGroup(Long notificationId, Long actorId) {

        int inserted = notificationActorRepository.InsertIgnore(notificationId, actorId);
        if(inserted > 0) {
            notificationRepository.incrementActor(notificationId, actorId);
        }
    }
}
