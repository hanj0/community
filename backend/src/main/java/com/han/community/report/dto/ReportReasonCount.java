package com.han.community.report.dto;

import com.han.community.report.entity.ReportReason;

public record ReportReasonCount(
        ReportReason reason,
        Long reasonCount
) {}
