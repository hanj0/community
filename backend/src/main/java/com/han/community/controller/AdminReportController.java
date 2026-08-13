package com.han.community.controller;

import com.han.community.dto.common.PageResponse;
import com.han.community.dto.common.SuccessResponse;
import com.han.community.dto.report.ReportDetailResponseDto;
import com.han.community.dto.report.ReportHistoryListResponseDto;
import com.han.community.dto.report.ReportResolveRequestDto;
import com.han.community.dto.report.ReportSummaryResponseDto;
import com.han.community.entity.User;
import com.han.community.entity.report.ReportStatus;
import com.han.community.entity.report.ReportTargetType;
import com.han.community.service.AdminReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/reports")
@RequiredArgsConstructor
public class AdminReportController {

    private final AdminReportService reportService;

    @GetMapping
    public ResponseEntity<PageResponse<ReportSummaryResponseDto>> getReports(
            @RequestParam(defaultValue = "count") String sort,
            @RequestParam ReportStatus reportStatus,
            Pageable pageable) {

        Page<ReportSummaryResponseDto> response = reportService.getReportPage(sort, reportStatus, pageable);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(PageResponse.of(response));
    }

    @GetMapping("/{targetType}/{targetId}/pending")
    public ResponseEntity<SuccessResponse<ReportDetailResponseDto>> getReport(
            @PathVariable ReportTargetType targetType,
            @PathVariable Long targetId) {

        ReportDetailResponseDto response = reportService.getReportDetail(targetType, targetId);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(SuccessResponse.of(response));
    }

    @GetMapping("/{targetType}/{targetId}/resolutions")
    public ResponseEntity<SuccessResponse<ReportHistoryListResponseDto>> getReportHistory(
            @PathVariable ReportTargetType targetType,
            @PathVariable Long targetId) {

        ReportHistoryListResponseDto response = reportService.getReportHistory(targetType, targetId);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(SuccessResponse.of(response));
    }

    @PostMapping("/{targetType}/{targetId}/resolutions")
    public ResponseEntity<SuccessResponse<Void>> resolveReports(
            @AuthenticationPrincipal User user,
            @PathVariable ReportTargetType targetType,
            @PathVariable Long targetId,
            @RequestBody ReportResolveRequestDto requestDto) {

        reportService.resolveReports(targetType, targetId, user.getId(), requestDto);

        return ResponseEntity
                .status(HttpStatus.OK)
                .build();
    }
}
