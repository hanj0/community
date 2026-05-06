package com.han.community.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;

import java.time.LocalDateTime;

@Entity
public class PostReaction {
    @Id
    private Long id;

    @ManyToOne
    private User user;
    @ManyToOne
    private Post post;

    private Character type;

    private LocalDateTime createdAt;
}
