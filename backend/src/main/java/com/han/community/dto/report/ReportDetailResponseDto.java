package com.han.community.dto.report;

import com.han.community.dto.UserSanctionInfo;
import com.han.community.entity.ReportReason;
import com.han.community.entity.ReportStatus;
import com.han.community.entity.ReportTargetType;

import java.time.LocalDateTime;
import java.util.List;

public record ReportDetailResponseDto(
        ReportTargetType targetType,
        Long targetId,
        long reportCount,
        ReportStatus status,
        ReportReason mainReason,
        LocalDateTime firstReportedAt,
        LocalDateTime lastReportedAt,
        Boolean updated,
        String snapshot,
        List<ReportReasonCount> reasonCounts,
        Long reportedUserId,
        UserSanctionInfo userSanctionInfo
        // todo: 처리완료 건은 이전처리이력을 보여줄만함
) {

    public static ReportDetailResponseDto from(ReportDetail reportDetail, List<ReportReasonCount> reasonCounts, UserSanctionInfo userSanctionInfo) {

        return new ReportDetailResponseDto(
                reportDetail.targetType(),
                reportDetail.targetId(),
                reportDetail.reportCount(),
                reportDetail.status(),
                reportDetail.mainReason(),
                reportDetail.firstReportedAt(),
                reportDetail.lastReportedAt(),
                reportDetail.contentUpdatedAt().isAfter(reportDetail.firstReportedAt()),
                reportDetail.snapshot(),
                reasonCounts,
                reportDetail.reportedUserId(),
                userSanctionInfo
        );
    }
}
