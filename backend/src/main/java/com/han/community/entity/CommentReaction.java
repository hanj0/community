package com.han.community.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class CommentReaction extends BaseTimeEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    private User user;
    @ManyToOne(fetch = FetchType.LAZY)
    private Comment comment;
    @Enumerated(EnumType.STRING)
    private ReactionType type;
}
