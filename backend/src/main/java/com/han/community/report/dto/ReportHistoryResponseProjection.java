package com.han.community.report.dto;

import com.han.community.report.entity.ReportReason;
import com.han.community.report.entity.ReportResolution;
import com.han.community.report.entity.ReportTargetType;

import java.time.LocalDateTime;

public interface ReportHistoryResponseProjection {
    ReportTargetType getTargetType();
    Long getTargetId();
    LocalDateTime getHandledAt();
    ReportResolution getResolution();
    long getReportCount();
    ReportReason getMainReason();
    LocalDateTime getFirstReportedAt();
    LocalDateTime getLastReportedAt();
    String getHandledBy();
    String getHandledMemo();
}
