package com.han.community.repository;

import com.han.community.entity.Notification;
import com.han.community.entity.NotificationType;
import com.han.community.entity.TargetType;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    @Query("""
SELECT n FROM Notification n
WHERE n.recipientId = :recipientId
    AND n.targetType = :targetType
    AND n.targetId = :targetId
    AND n.type = :type
    AND n.isRead = false
""")
    Optional<Notification> findActiveGroup(@Param("recipientId")Long recipientId, @Param("targetType")TargetType targetType,
                                           @Param("targetId")Long targetId, @Param("type")NotificationType type);

    @Modifying
    @Query("""
UPDATE Notification n
SET n.lastActorId = :actorId,
    n.actorCount = n.actorCount + 1,
    n.updatedAt = CURRENT_TIMESTAMP
WHERE n.id = :notificationId
""")
    void incrementActor(@Param("notificationId")Long notificationId, @Param("actorId")Long actorId);

    long countByRecipientIdAndIsReadFalse(Long recipientId);

    @Query("""
SELECT n FROM Notification n
WHERE n.recipientId = :recipientId
    AND (CAST(:cursorUpdatedAt AS timestamp) IS NULL
        OR n.updatedAt < :cursorUpdatedAt
        OR (n.updatedAt = :cursorUpdatedAt AND n.id < :cursorId))
ORDER BY n.updatedAt DESC, n.id DESC
""")
    List<Notification> findByCursor(@Param("recipientId")Long recipientId, @Param("cursorUpdatedAt")LocalDateTime cursorUpdatedAt, @Param("cursorId")Long cursorId, Pageable pageable);

    @Modifying
    @Query("""
UPDATE Notification n
SET n.isRead = true
WHERE n.id = :notificationId
    AND n.recipientId = :recipientId
    AND n.isRead = false
""")
    void markRead(@Param("recipientId")Long recipientId, @Param("notificationId")Long notificationId);


    @Modifying
    @Query("""
UPDATE Notification n
SET n.isRead = true
WHERE n.recipientId = :recipientId
    AND n.isRead = false
""")
    void markAllRead(@Param("recipientId")Long recipientId);
}
