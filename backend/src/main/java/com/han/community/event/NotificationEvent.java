package com.han.community.event;

import com.han.community.entity.NotificationType;
import com.han.community.entity.TargetType;

public record NotificationEvent(
        Long actorId,
        TargetType targetType,
        Long targetId,
        Long rootPostId,
        Long recipientId,
        NotificationType type,
        String contentPreview
){

    private static final int PREVIEW_LENGTH = 30;

    public NotificationEvent {
        contentPreview = truncate(contentPreview);
    }

    // todo: 이모지가 경계에 포함된 경우를 고려해서 처리해야함
    private String truncate(String content) {

        if(content == null) return null;
        return content.length() > PREVIEW_LENGTH
                ? content.substring(0, PREVIEW_LENGTH)
                : content;
    }
}
