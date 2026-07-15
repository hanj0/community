package com.han.community.repository;

import com.han.community.entity.NotificationActor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface NotificationActorRepository extends JpaRepository<NotificationActor, Long> {

    @Modifying
    @Query(value = """
INSERT INTO Notification_actor (notification_id, actor_id, created_at)
VALUES (:notificationId, :actorId, CURRENT_TIMESTAMP)
ON CONFLICT (notification_id, actor_id) DO NOTHING
""", nativeQuery = true)
    int InsertIgnore(@Param("notificationId")Long notificationId, @Param("actorId")Long actorId);
}
