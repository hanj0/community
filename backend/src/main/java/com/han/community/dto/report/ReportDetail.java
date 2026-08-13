package com.han.community.dto.report;

import com.han.community.entity.report.ReportReason;
import com.han.community.entity.report.ReportStatus;
import com.han.community.entity.report.ReportTargetType;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

public record ReportDetail(
        LocalDateTime contentUpdatedAt,
        String content,
        List<Long> reportIds,
        Long reportCount,
        ReportTargetType targetType,
        Long targetId,
        Long reportedUserId,
        ReportStatus status,
        LocalDateTime firstReportedAt,
        LocalDateTime lastReportedAt,
        ReportReason mainReason,
        String snapshot,
        LocalDateTime handledAt
) {

    public static ReportDetail from(ReportDetailProjection p) {
        return new ReportDetail(
                p.getContentUpdatedAt(),
                p.getContent(),
                parseIds(p.getReportIds()),
                p.getReportCount(),
                p.getTargetType(),
                p.getTargetId(),
                p.getReportedUserId(),
                p.getStatus(),
                p.getFirstReportedAt(),
                p.getLastReportedAt(),
                p.getMainReason(),
                p.getSnapshot(),
                p.getHandledAt()
        );
    }

    private static List<Long> parseIds(String ids) {

        if(ids == null || ids.isBlank()) return List.of();
        return Arrays.stream(ids.split(","))
                .map(str -> Long.valueOf(str))
                .toList();
    }
}
