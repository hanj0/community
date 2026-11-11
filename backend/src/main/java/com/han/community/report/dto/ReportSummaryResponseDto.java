package com.han.community.report.dto;

import com.han.community.report.entity.ReportReason;
import com.han.community.report.entity.ReportStatus;
import com.han.community.report.entity.ReportTargetType;

import java.time.LocalDateTime;

public record ReportSummaryResponseDto(
        Long reportCount,
        ReportTargetType targetType,
        Long targetId,
        String targetPreview,
        Long reportedUserId,
        String reportedUsername,
        ReportReason mainReason,
        LocalDateTime lastReportedAt,
        ReportStatus status
) {
    public static ReportSummaryResponseDto from(ReportSummaryResponseProjection p) {
        return new ReportSummaryResponseDto(
                p.getReportCount(),
                p.getTargetType(),
                p.getTargetId(),
                p.getTargetPreview(),
                p.getReportedUserId(),
                p.getReportedUsername(),
                p.getMainReason(),
                p.getLastReportedAt(),
                p.getStatus()
        );
    }
}
