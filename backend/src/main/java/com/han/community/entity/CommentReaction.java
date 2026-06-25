package com.han.community.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@Table(
        name = "comment_reaction",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_comment_reaction_user_comment",
                columnNames = {"comment_id", "user_id"}
        )
)
public class CommentReaction extends BaseTimeEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comment_id")
    private Comment comment;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
    @Enumerated(EnumType.STRING)
    private ReactionType type;

    public CommentReaction(Comment comment, User user, ReactionType type) {
        this.comment = comment;
        this.user = user;
        this.type = type;
    }

    public void changeType(ReactionType type) {
        this.type = type;
    }
}
