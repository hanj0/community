package com.han.community.comment.dto;

import com.han.community.post.entity.ReactionType;
import com.han.community.user.dto.UserDto;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class CommentResponse {

    private Long id;
    private String content;
    private Long parentId;
    private UserDto.AuthorResponse authorInfo;
    private int likeCount;
    private int dislikeCount;
    private int replyCount;
    private ReactionType reactionType;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
