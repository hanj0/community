package com.han.community.bookmark.entity;

import com.han.community.global.entity.BaseTimeEntity;
import com.han.community.user.entity.User;
import com.han.community.post.entity.Post;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@Table(
        name = "bookmark",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_bookmark_post_user",
                columnNames = {"post_id", "user_id"}
        )
)
public class Bookmark extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "post_id")
    private Post post;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    public Bookmark(Post post, User user) {
        this.post = post;
        this.user = user;
    }
}
