package com.han.community.report.controller;

import com.han.community.global.dto.PageResponse;
import com.han.community.global.dto.SuccessResponse;
import com.han.community.report.dto.ReportDetailResponseDto;
import com.han.community.report.dto.ReportHistoryListResponseDto;
import com.han.community.report.dto.ReportResolveRequestDto;
import com.han.community.report.dto.ReportSummaryResponseDto;
import com.han.community.user.entity.User;
import com.han.community.report.entity.ReportStatus;
import com.han.community.report.entity.ReportTargetType;
import com.han.community.report.service.AdminReportService;
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
