package com.han.community.report.dto;

import com.han.community.report.entity.ReportReason;
import com.han.community.report.entity.ReportResolution;
import com.han.community.report.entity.ReportTargetType;

import java.time.LocalDateTime;

public record ReportHistoryResponseDto(
        ReportTargetType targetType,
        Long targetId,
        LocalDateTime handledAt,
        ReportResolution resolution,
        long reportCount,
        ReportReason mainReason,
        LocalDateTime firstReportedAt,
        LocalDateTime lastReportedAt,
        String handledBy,
        String handledMemo
) {
    public static ReportHistoryResponseDto from(ReportHistoryResponseProjection p) {
        return new ReportHistoryResponseDto(
                p.getTargetType(),
                p.getTargetId(),
                p.getHandledAt(),
                p.getResolution(),
                p.getReportCount(),
                p.getMainReason(),
                p.getFirstReportedAt(),
                p.getLastReportedAt(),
                p.getHandledBy(),
                p.getHandledMemo()
        );
    }
}
