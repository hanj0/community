package com.han.community.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
/// ddl-auto: none으로 바꾸면서 아래 애너테이션도 삭제하고 직접설정해야함
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
