package com.han.community.service;

import com.han.community.dto.report.ReportRequestDto;
import com.han.community.dto.report.ReportResponseDto;
import com.han.community.entity.*;
import com.han.community.entity.report.Report;
import com.han.community.entity.report.ReportTargetType;
import com.han.community.global.exception.BusinessException;
import com.han.community.global.exception.ErrorCode;
import com.han.community.repository.CommentRepository;
import com.han.community.repository.PostRepository;
import com.han.community.repository.ReportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final ReportRepository reportRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;

    @Transactional
    public ReportResponseDto report(Long userId, ReportRequestDto requestDto) {

        if(requestDto.reason().requiresDetail() && (requestDto.reasonDetail() == null || requestDto.reasonDetail().isBlank()))
            throw new BusinessException(ErrorCode.DETAIL_REQUIRED);

        Object target = getTarget(requestDto.targetType(), requestDto.targetId());

        Long reportedUserId;
        String snapshot;
        if(target instanceof Post post) {
            reportedUserId = post.getUser().getId();
            snapshot = post.getContent();
        }
        else if(target instanceof Comment comment) {
            reportedUserId = comment.getUser().getId();
            snapshot = comment.getContent();
        }
        else {
            throw new IllegalStateException("신고대상이 올바르지 않습니다.");
        }

        Report report = Report.builder()
                .reporterId(userId)
                .targetType(requestDto.targetType())
                .targetId(requestDto.targetId())
                .reportedUserId(reportedUserId)
                .reason(requestDto.reason())
                .reasonDetail(requestDto.reasonDetail())
                .targetContentSnapshot(snapshot)
                .build();

        Report saved = reportRepository.save(report);

        return new ReportResponseDto(saved.getId());
    }

    private Object getTarget(ReportTargetType type, Long id) {

        return switch(type) {
            case POST -> postRepository.findById(id)
                    .orElseThrow(() -> new BusinessException(ErrorCode.POST_NOT_FOUND));
            case COMMENT ->  commentRepository.findById(id)
                        .orElseThrow(() -> new BusinessException(ErrorCode.COMMENT_NOT_FOUND));
        };
    }
}
