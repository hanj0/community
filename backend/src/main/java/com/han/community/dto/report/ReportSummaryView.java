package com.han.community.dto.report;

import com.han.community.entity.ReportReason;
import com.han.community.entity.ReportStatus;
import com.han.community.entity.ReportTargetType;

import java.time.LocalDateTime;

public interface ReportSummaryView {
    Long getReportCount();
    ReportTargetType getTargetType();
    Long getTargetId();
    String getTargetPreview();
    Long getReportedUserId();
    String getReportedUsername();
    ReportReason getMainReason();
    LocalDateTime getLastReportedAt();
    ReportStatus getStatus();
}
