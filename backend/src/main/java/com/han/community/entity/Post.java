package com.han.community.entity;

import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDateTime;

@Entity
@Getter
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Channel channel;
    @ManyToOne
    private User user;

    private String title;
    @Lob
    private String content;

    private int viewCount;
    private int likeCount;
    private int dislikeCount;
    private int CommentCount;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;

    protected Post() {}


    public void update(String title, String content) {
        this.title = title;
        this.content = content;
    }



    // builder 패턴
    private Post(Builder builder) {
        this.title = builder.title;
        this.content = builder.content;
    }

    public static class Builder {

        private String title;
        private String content;

        public Builder title(String title) {
            this.title = title;
            return this;
        }
        public Builder content(String content) {
            this.content = content;
            return this;
        }
        public Post build() {
            return new Post(this);
        }
    }
}
