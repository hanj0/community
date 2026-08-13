package com.han.community.dto.report;

import com.han.community.entity.report.ReportReason;
import com.han.community.entity.report.ReportStatus;
import com.han.community.entity.report.ReportTargetType;

import java.time.LocalDateTime;

public interface ReportSummaryResponseProjection {

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
