package com.han.community.service;

import com.han.community.dto.report.ReportRequest;
import com.han.community.dto.report.ReportResponse;
import com.han.community.entity.*;
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
    public ReportResponse report(Long userId, ReportRequest requestDto) {

        if(requestDto.reason().requiresDetail() && (requestDto.reasonDetail() == null || requestDto.reasonDetail().isBlank()))
            throw new BusinessException(ErrorCode.INVALID_REQUEST);

        Object target = getTarget(requestDto.targetType(), requestDto.targetId());

        Long ownerId;
        String snapshot;
        if(target instanceof Post post) {
            ownerId = post.getUser().getId();
            snapshot = post.getContent();
        }
        else if(target instanceof Comment comment) {
            ownerId = comment.getUser().getId();
            snapshot = comment.getContent();
        }
        else {
            throw new IllegalStateException("신고대상이 올바르지 않습니다.");
        }

        Report report = Report.builder()
                .reporterId(userId)
                .targetType(requestDto.targetType())
                .targetId(requestDto.targetId())
                .targetOwnerId(ownerId)
                .reason(requestDto.reason())
                .reasonDetail(requestDto.reasonDetail())
                .targetContentSnapshot(snapshot)
                .build();

        Report saved = reportRepository.save(report);

        return new ReportResponse(saved.getId());
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
