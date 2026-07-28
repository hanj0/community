package com.han.community.service;

import com.han.community.dto.report.ReportSummaryResponseDto;
import com.han.community.entity.ReportStatus;
import com.han.community.global.exception.BusinessException;
import com.han.community.global.exception.ErrorCode;
import com.han.community.repository.ReportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminReportService {

    private final ReportRepository reportRepository;

    public Page<ReportSummaryResponseDto> getReportPage(String sort, ReportStatus status, Pageable pageable) {

        Sort pageSort = switch(sort) {
            case "count" -> Sort.by(Sort.Direction.DESC, "report_count");
            case "latest" -> Sort.by(Sort.Direction.DESC, "last_reported_at");
            default -> throw new BusinessException(ErrorCode.INVALID_REQUEST);
        };
        pageable = PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                pageSort
        );

        return findReportPage(status, pageable);
    }

    private Page<ReportSummaryResponseDto> findReportPage(ReportStatus status, Pageable pageable) {
        // todo: 동적쿼리 도입하면 status에 따라 조건문 동적으로
        if(status == null) return reportRepository.findReportPageImpl(pageable)
                .map(ReportSummaryResponseDto::from);
        return reportRepository.findReportPageImpl(status.name(), pageable)
                .map(v -> ReportSummaryResponseDto.from(v));
    }
}
