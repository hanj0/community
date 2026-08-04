package com.han.community.repository;

import com.han.community.dto.report.ReportDetailProjection;
import com.han.community.dto.report.ReportReasonCount;
import com.han.community.dto.report.ReportSummaryResponseProjection;
import com.han.community.entity.Report;
import com.han.community.entity.ReportStatus;
import com.han.community.entity.ReportTargetType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ReportRepository extends JpaRepository<Report, Long> {

    @Query(value = """
SELECT
    summary.report_count AS reportCount,
    summary.target_type AS targetType,
    summary.target_id AS targetId,
    COALESCE(p.content, c.content) AS targetPreview,
    summary.reported_user_id AS reportedUserId,
    u.username AS reportedUsername,
    summary.main_reason AS mainReason,
    summary.last_reported_at AS lastReportedAt,
    summary.status AS status
FROM
    (SELECT
        COUNT(*) AS report_count,
        r.target_type,
        r.target_id,
        r.reported_user_id,
        r.status,
        MAX(r.created_at) AS last_reported_at,
        MODE() within GROUP (ORDER BY reason) AS main_reason
    FROM Report r
    GROUP BY r.target_type, r.target_id, r.reported_user_id, r.status) AS summary
INNER JOIN Users u ON u.id = summary.reported_user_id
LEFT JOIN Post p ON summary.target_type = 'POST' AND p.id = summary.target_id
LEFT JOIN Comment c ON summary.target_type = 'COMMENT' AND c.id = summary.target_id
WHERE (CAST(:status AS text) IS NULL OR summary.status = :status)
""",
countQuery = """
SELECT COUNT(*)
FROM(
    SELECT r.status
    FROM report r
    GROUP BY r.target_type, r.target_id, r.reported_user_id, r.status
    ) AS summary
WHERE (CAST(:status AS text) IS NULL OR summary.status = :status)
""", nativeQuery = true)
    Page<ReportSummaryResponseProjection> findReportPageInternal(@Param("status")String status, Pageable pageable);

    @Query(value = """
SELECT
COALESCE(p.updated_at, c.updated_at) content_updated_at,
COALESCE(p.content, c.content) content,
summary.report_count reportCount,
summary.target_type targetType,
summary.target_id targetId,
summary.reported_user_id reportedUserId,
summary.status status,
summary.first_reported_at firstReportedAt,
summary.last_reported_at lastReportedAt,
summary.main_reason mainReason,
summary.snapshot snapshot
FROM (
	SELECT
		COUNT(r.id) report_count,
		r.target_type,
		r.target_id,
		r.reported_user_id,
		r.status,
		MIN(r.created_at) first_reported_at,
		MAX(r.created_at) last_reported_at,
		MODE() WITHIN GROUP (ORDER BY reason) main_reason,
		(SELECT target_content_snapshot FROM Report WHERE target_type = :targetType AND target_id = :targetId AND status = 'PENDING' ORDER BY created_at ASC LIMIT 1) snapshot
		-- (ARRAY_AGG(r.target_content_snapshot ORDER BY created_at ASC))[1] AS snapshot
	FROM Report r
	WHERE r.target_type = :targetType
	    AND r.target_id = :targetId AND r.status = 'PENDING'
	GROUP BY r.target_type, r.target_id, r.reported_user_id, r.status) AS summary
-- INNER JOIN Users u ON u.id = summary.reported_user_id
LEFT JOIN Post p ON summary.target_type = 'POST' AND p.id = summary.target_id
LEFT JOIN Comment c ON summary.target_type = 'COMMENT' AND c.id = summary.target_id
ORDER BY report_count DESC
""", nativeQuery = true)
    Optional<ReportDetailProjection> findReportDetailInternal(@Param("targetType")String targetType, @Param("targetId")Long targetId);

    @Query("""
SELECT
    r.reason,
    COUNT(r) reasonCount
FROM Report r
WHERE r.targetType = :targetType
    AND r.targetId = :targetId
    AND r.status = 'PENDING'
GROUP BY r.reason
ORDER BY COUNT(r) DESC
""")
    List<ReportReasonCount> findReportReasonCount(@Param("targetType")ReportTargetType targetType, @Param("targetId")Long targetId);


    default Page<ReportSummaryResponseProjection> findReportPage(@Param("status") ReportStatus status, Pageable pageable) {
        return findReportPageInternal(status == null ? null : status.name(), pageable);
    }

    default Optional<ReportDetailProjection> findReportDetail(ReportTargetType targetType, Long targetId) {
        return findReportDetailInternal(targetType == null ? null : targetType.name(), targetId);
    }
}
