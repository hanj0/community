package com.han.community.dto.report;

import com.han.community.entity.ReportReason;
import com.han.community.entity.ReportStatus;
import com.han.community.entity.ReportTargetType;

import java.time.LocalDateTime;

public interface ReportDetailProjection {

    LocalDateTime getContentUpdatedAt();
    String getContent();
    Long getReportCount();
    ReportTargetType getTargetType();
    Long getTargetId();
    Long getReportedUserId();
    ReportStatus getStatus();
    LocalDateTime getFirstReportedAt();
    LocalDateTime getLastReportedAt();
    ReportReason getMainReason();
    String getSnapshot();
}
