package com.han.community.controller;

import com.han.community.dto.common.SuccessResponse;
import com.han.community.dto.report.ReportRequest;
import com.han.community.dto.report.ReportResponse;
import com.han.community.entity.User;
import com.han.community.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    @PostMapping
    public ResponseEntity<SuccessResponse<ReportResponse>> postReport(
            @AuthenticationPrincipal User user,
            @RequestBody ReportRequest requestDto) {

        ReportResponse response = reportService.report(user.getId(), requestDto);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(SuccessResponse.of(response));
    }
}
