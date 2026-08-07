package com.han.community.service;

import com.han.community.dto.UserSanctionInfo;
import com.han.community.dto.report.*;
import com.han.community.entity.ReportStatus;
import com.han.community.entity.ReportTargetType;
import com.han.community.global.exception.BusinessException;
import com.han.community.global.exception.ErrorCode;
import com.han.community.repository.ReportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminReportService {

    private final ReportRepository reportRepository;

    @Transactional
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

        return reportRepository.findReportPage(status, pageable)
                .map(p -> ReportSummaryResponseDto.from(p));
    }

    @Transactional
    public ReportDetailResponseDto getReportDetail(ReportTargetType targetType, Long targetId) {

        ReportDetail reportDetail = ReportDetail.from(
                reportRepository.findReportDetail(targetType, targetId, false)
                        .orElseThrow(() -> new IllegalStateException("ReportDetail must exist"))
        );

        List<ReportReasonCount> reasonCounts = reportRepository.findReportReasonCount(targetType, targetId);
        UserSanctionInfo userSanctionInfo = null;

        return ReportDetailResponseDto.from(reportDetail, reasonCounts, userSanctionInfo);
    }

    @Transactional
    public ReportHistoryListResponseDto getReportHistory(ReportTargetType targetType, Long targetId) {

        List<ReportHistoryResponseDto> reportHistories =
                reportRepository.findReportHistoryList(targetType, targetId).stream()
                        .map(p -> ReportHistoryResponseDto.from(p))
                        .toList();

        return ReportHistoryListResponseDto.from(reportHistories);
    }

}
