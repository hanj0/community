package com.han.community.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(
        name = "report",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_report",
                columnNames = {"reporter_id", "target_type", "target_id"}
        )
)
public class Report extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long reporterId;
    @Enumerated(EnumType.STRING)
    private ReportTargetType targetType;
    private Long targetId;
    private Long targetOwnerId;
    @Enumerated(EnumType.STRING)
    private ReportReason reason;
    private String reasonDetail;
    @Column(columnDefinition = "TEXT")
    private String targetContentSnapshot;
    @Enumerated(EnumType.STRING)
    private ReportStatus status;
    private Long handledBy;
    private LocalDateTime handledAt;

    @Builder
    private Report(Long reporterId, ReportTargetType targetType, Long targetId, Long targetOwnerId,
                   ReportReason reason, String reasonDetail, String targetContentSnapshot) {

        this.reporterId = reporterId;
        this.targetType = targetType;
        this.targetId = targetId;
        this.targetOwnerId = targetOwnerId;
        this.reason = reason;
        this.reasonDetail = reasonDetail;
        this.targetContentSnapshot = targetContentSnapshot;
        this.status = ReportStatus.PENDING;

    }
}
