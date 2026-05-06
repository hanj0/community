package com.han.community.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;

import java.time.LocalDateTime;

@Entity
public class CommentReaction {

    @Id
    private Long id;

    @ManyToOne
    private User user;
    @ManyToOne
    private Comment comment;

    private Character type;

    private LocalDateTime createdAt;
}
