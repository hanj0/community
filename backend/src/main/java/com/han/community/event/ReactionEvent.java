package com.han.community.event;

import com.han.community.entity.TargetType;

public record ReactionEvent(
        Long actorId,
        TargetType targetType,
        Long targetId,
        Long rootPostId,
        Long recipientId
) {}
