package com.han.community.dto.report;

import com.han.community.entity.ReportReason;

public record ReportReasonCount(
        ReportReason reason,
        Long reasonCount
) {}
