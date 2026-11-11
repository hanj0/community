package com.han.community.report.controller;

import com.han.community.global.dto.SuccessResponse;
import com.han.community.report.dto.ReportRequestDto;
import com.han.community.report.dto.ReportResponseDto;
import com.han.community.user.entity.User;
import com.han.community.report.service.ReportService;
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
    public ResponseEntity<SuccessResponse<ReportResponseDto>> postReport(
            @AuthenticationPrincipal User user,
            @RequestBody ReportRequestDto requestDto) {

        ReportResponseDto response = reportService.report(user.getId(), requestDto);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(SuccessResponse.of(response));
    }
}
