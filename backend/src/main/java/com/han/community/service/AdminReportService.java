package com.han.community.service;

import com.han.community.dto.UserSanctionInfo;
import com.han.community.dto.report.*;
import com.han.community.entity.report.ReportResolution;
import com.han.community.entity.report.ReportStatus;
import com.han.community.entity.report.ReportTargetType;
import com.han.community.entity.sanction.Sanction;
import com.han.community.entity.sanction.SanctionSource;
import com.han.community.global.exception.BusinessException;
import com.han.community.global.exception.ErrorCode;
import com.han.community.repository.CommentRepository;
import com.han.community.repository.PostRepository;
import com.han.community.repository.ReportRepository;
import com.han.community.repository.SanctionRepository;
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
    private final SanctionRepository sanctionRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;

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

    @Transactional
    public void resolveReports(ReportTargetType targetType, Long targetId, Long adminId, ReportResolveRequestDto requestDto) {

        // 신고처리 직전 컨텐츠가 삭제된 경우 삭제로 처리, 이미 삭제된 컨텐츠 알림
        int updated = reportRepository.updateStatusAndResolutionByIds(
                requestDto.reportIds(), requestDto.contentActionType(), adminId, requestDto.reasonDetail()
        );
        if(updated == 0) {
            // 이미 처리된 신고
            return;
        }
        if(requestDto.contentActionType() == ReportResolution.REJECTED) return;

        Long reportedUserId;
        if(targetType == ReportTargetType.POST) {
            reportedUserId = postRepository.findUserIdById(targetId);
            postRepository.deleteById(targetId);
        }
        else if(targetType == ReportTargetType.COMMENT) {
            reportedUserId = commentRepository.findUserIdById(targetId);
            commentRepository.deleteById(targetId);
        }
        else {
            throw new IllegalArgumentException("지원하지 않는 신고대상 타입 : " + targetType);
        }
        // affected-row 받아서 이미 삭제된 컨텐츠 입니다. 알림

        if(requestDto.sanction() == null) return;

        Sanction sanction = Sanction.create(
                reportedUserId,
                requestDto.sanction().sanctionType(),
                requestDto.reason(),
                requestDto.reasonDetail(),
                SanctionSource.REPORT,
                targetId,
                adminId
        );

        sanctionRepository.save(sanction);

        return;
        // 이미 처리된 신고인지, 이미 삭제된 컨텐츠인지, 제재가 며칠 적용됐는지 반환해야함
    }
}
