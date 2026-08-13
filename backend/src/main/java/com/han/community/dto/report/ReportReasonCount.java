package com.han.community.dto.report;

import com.han.community.entity.report.ReportReason;

public record ReportReasonCount(
        ReportReason reason,
        Long reasonCount
) {}
