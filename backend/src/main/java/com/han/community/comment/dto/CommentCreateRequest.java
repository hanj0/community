package com.han.community.comment.dto;

public record CommentCreateRequest(
        Long parentId,
        String content
) {
}
