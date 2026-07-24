package com.han.community.dto.report;

import com.han.community.entity.ReportReason;
import com.han.community.entity.ReportTargetType;

public record ReportRequest(
        ReportTargetType targetType,
        Long targetId,
        ReportReason reason,
        String reasonDetail
) {}
