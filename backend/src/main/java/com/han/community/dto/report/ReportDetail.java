package com.han.community.dto.report;

import com.han.community.entity.ReportReason;
import com.han.community.entity.ReportStatus;
import com.han.community.entity.ReportTargetType;

import java.time.LocalDateTime;

public record ReportDetail(
        LocalDateTime contentUpdatedAt,
        String content,
        Long reportCount,
        ReportTargetType targetType,
        Long targetId,
        Long reportedUserId,
        ReportStatus status,
        LocalDateTime firstReportedAt,
        LocalDateTime lastReportedAt,
        ReportReason mainReason,
        String snapshot
) {

    public static ReportDetail from(ReportDetailProjection p) {
        return new ReportDetail(
                p.getContentUpdatedAt(),
                p.getContent(),
                p.getReportCount(),
                p.getTargetType(),
                p.getTargetId(),
                p.getReportedUserId(),
                p.getStatus(),
                p.getFirstReportedAt(),
                p.getLastReportedAt(),
                p.getMainReason(),
                p.getSnapshot()
        );
    }
}
