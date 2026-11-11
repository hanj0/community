package com.han.community.report.dto;

import com.han.community.report.entity.ReportReason;
import com.han.community.report.entity.ReportTargetType;

public record ReportRequestDto(
        ReportTargetType targetType,
        Long targetId,
        ReportReason reason,
        String reasonDetail
) {}
