package com.han.community.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@Table(
        name = "notification_actor",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_notification_actor",
                columnNames = {"notification_id", "actor_id"}
        )
)
public class NotificationActor extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private Long notificationId;
    @Column(nullable = false)
    private Long actorId;
}
