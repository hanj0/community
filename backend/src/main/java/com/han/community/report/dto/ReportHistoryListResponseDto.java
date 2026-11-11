package com.han.community.report.dto;

import java.util.List;

public record ReportHistoryListResponseDto(
        List<ReportHistoryResponseDto> histories,
        int totalCount
) {
    public static ReportHistoryListResponseDto from(List<ReportHistoryResponseDto> list) {
        return new ReportHistoryListResponseDto(list, list.size());
    }
}
