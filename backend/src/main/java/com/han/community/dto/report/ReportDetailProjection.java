package com.han.community.dto.report;

import com.han.community.entity.report.ReportReason;
import com.han.community.entity.report.ReportStatus;
import com.han.community.entity.report.ReportTargetType;

import java.time.LocalDateTime;

public interface ReportDetailProjection {

    LocalDateTime getContentUpdatedAt();
    String getContent();
    String getReportIds();
    Long getReportCount();
    ReportTargetType getTargetType();
    Long getTargetId();
    Long getReportedUserId();
    ReportStatus getStatus();
    LocalDateTime getFirstReportedAt();
    LocalDateTime getLastReportedAt();
    ReportReason getMainReason();
    String getSnapshot();
    LocalDateTime getHandledAt();
}
