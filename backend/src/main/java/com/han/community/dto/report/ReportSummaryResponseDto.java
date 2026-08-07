package com.han.community.dto.report;

import com.han.community.entity.ReportReason;
import com.han.community.entity.ReportStatus;
import com.han.community.entity.ReportTargetType;

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
