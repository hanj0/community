package com.han.community.dto.report;

import com.han.community.entity.ReportReason;
import com.han.community.entity.ReportResolution;
import com.han.community.entity.ReportTargetType;

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
