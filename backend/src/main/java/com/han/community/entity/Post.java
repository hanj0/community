package com.han.community.entity;

import com.han.community.dto.PostDto;
import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Getter
public class Post extends BaseSoftDeleteEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "channel_id")
    private Channel channel;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    private String title;
    @Lob
    private String content;

    private int viewCount;
    private int likeCount;
    private int dislikeCount;
    private int CommentCount;

    protected Post() {}

    public void update(PostDto.UpdateRequest requestDto) {
        this.title = requestDto.getTitle();
        this.content = requestDto.getContent();
    }



    // builder 패턴
    private Post(Builder builder) {
        this.channel = builder.channel;
        this.user = builder.user;
        this.title = builder.title;
        this.content = builder.content;
    }

    public static class Builder {

        private Channel channel;
        private User user;
        private String title;
        private String content;

        public Builder channel(Channel channel) {
            this.channel = channel;
            return this;
        }
        public Builder user(User user) {
            this.user = user;
            return this;
        }
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
