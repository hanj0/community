package com.han.community.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class PostReaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private User user;
    @ManyToOne
    private Post post;

    private Character type;

    private LocalDateTime createdAt;
}
