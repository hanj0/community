package com.han.community.dto.report;

import com.han.community.entity.report.ReportReason;
import com.han.community.entity.report.ReportTargetType;

public record ReportRequestDto(
        ReportTargetType targetType,
        Long targetId,
        ReportReason reason,
        String reasonDetail
) {}
