package com.han.community.report.dto;

import com.han.community.report.entity.ReportReason;
import com.han.community.report.entity.ReportStatus;
import com.han.community.report.entity.ReportTargetType;

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
