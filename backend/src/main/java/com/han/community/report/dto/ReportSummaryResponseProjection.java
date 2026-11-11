package com.han.community.report.dto;

import com.han.community.report.entity.ReportReason;
import com.han.community.report.entity.ReportStatus;
import com.han.community.report.entity.ReportTargetType;

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
