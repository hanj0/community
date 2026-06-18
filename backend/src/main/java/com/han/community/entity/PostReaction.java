package com.han.community.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@Table(
        name = "post_reaction",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_post_reaction_user_post",
                columnNames = {"post_id", "user_id"}
        )
)
public class PostReaction extends BaseTimeEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
    @Enumerated(EnumType.STRING)
    private ReactionType type;

    public PostReaction(Post post, User user, ReactionType type) {
        this.post = post;
        this.user = user;
        this.type = type;
    }

    public void changeType(ReactionType type) {
        this.type = type;
    }
}
