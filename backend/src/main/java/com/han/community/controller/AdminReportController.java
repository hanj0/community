package com.han.community.controller;

import com.han.community.dto.common.PageResponse;
import com.han.community.dto.report.ReportSummaryResponseDto;
import com.han.community.entity.Report;
import com.han.community.entity.ReportStatus;
import com.han.community.entity.User;
import com.han.community.service.AdminReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/reports")
@RequiredArgsConstructor
public class AdminReportController {

    private final AdminReportService reportService;

    @GetMapping
    public ResponseEntity<PageResponse<ReportSummaryResponseDto>> getReports(
            @AuthenticationPrincipal User user,
            @RequestParam(defaultValue = "count") String sort,
            @RequestParam ReportStatus reportStatus,
            Pageable pageable) {

        Page<ReportSummaryResponseDto> response = reportService.getReportPage(sort, reportStatus, pageable);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(PageResponse.of(response));
    }
}
